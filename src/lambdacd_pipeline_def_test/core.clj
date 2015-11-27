(ns lambdacd-pipeline-def-test.core
  (require [lambdacd.steps.control-flow :as cflow]
           [lambdacd.steps.git :as git]
           [clojure.tools.logging :as log]
           [clojure.test :as test]))

(def default-meta-steps `(cflow/either
                           cflow/in-cwd
                           cflow/in-parallel
                           cflow/junction
                           cflow/run
                           cflow/alias
                           git/with-git
                           git/with-git-branch))

(declare test-step)

(defn- file-and-line
  [^Throwable exception depth]
  (let [stacktrace (.getStackTrace exception)]
    (if (< depth (count stacktrace))
      (let [^StackTraceElement s (nth stacktrace depth)]
        {:file (.getFileName s) :line (.getLineNumber s)})
      {:file nil :line nil})))

(defn report-test [msg-atom]
  (if (not-empty @msg-atom)
    (let [m (file-and-line (new java.lang.Throwable) 1)]
      (test/with-test-out
        (test/inc-report-counter :fail)
        (println "\nFAIL in" (test/testing-vars-str m))
        (when (seq test/*testing-contexts*) (println (test/testing-contexts-str)))
        (doall (map #(println "  " %) @msg-atom))))
    (test/inc-report-counter :pass)))

(defn report-fail [msg-atom mode msg]
  (when (= mode :success)
    (swap! msg-atom conj msg)))

(defn positions
  [pred coll]
  (keep-indexed (fn [idx x]
                  (when (pred x)
                    idx))
                coll))

(defn calc-var-arg-sym-pos [l]
  (first (positions #{'&} l)))

(defn is-valid-arg-count? [res-fn arg-count arg-list]
  (let [var-arg-sym-pos (calc-var-arg-sym-pos arg-list)]
    (if (nil? var-arg-sym-pos)
      (= arg-count (count arg-list))
      (>= arg-count var-arg-sym-pos))))

(defn is-in-meta-step-list? [meta-steps s]
  (some #{s} meta-steps))

(defn is-valid-fn-call? [msg-atom mode meta-steps l]
  (let [given-args (rest l)
        given-args-count (count given-args)
        fn-sym (first l)
        is-fn-resolvable (boolean (resolve fn-sym))]
    (if (not is-fn-resolvable)
      (do
        (report-fail msg-atom mode (str fn-sym ": undefinied"))
        true)
      (let [res-fn (resolve fn-sym)
            is-meta-step (or (:meta-step (meta res-fn)) (is-in-meta-step-list? meta-steps fn-sym))]
        (if (not is-meta-step)
          (do
            (report-fail msg-atom mode (str fn-sym ": not a meta function"))
            false)
          (let [fn-arg-lists (:arglists (meta res-fn))
                res (reduce (fn [old new] (or old (is-valid-arg-count? res-fn given-args-count new))) false fn-arg-lists)]
            (or res
                (do
                  (report-fail msg-atom mode (str fn-sym ": wrong number of args"))
                  false))))))))

(defn- test-list [msg-atom mode meta-steps l]
  (and (is-valid-fn-call? msg-atom mode meta-steps l)
       (every?
         identity
         (doall (map (partial test-step msg-atom mode meta-steps) (rest l))))))

(defn- test-symbol [msg-atom mode meta-steps s]
  (let [r (boolean (resolve s))]
    (if (not r)
      (report-fail msg-atom mode (str s ": undefinied"))
      (let [res-fn (resolve s)
            is-meta-step (or (:meta-step (meta res-fn)) (is-in-meta-step-list? meta-steps s))]
        (if is-meta-step
          (do
            (report-fail msg-atom mode (str s ": wrong number of args"))
            false)
          true)))))

(defn- test-element [msg-atom mode meta-steps x]
  (or (not (instance? clojure.lang.Symbol x))
      (test-symbol msg-atom mode meta-steps x)))

(defn test-step [msg-atom mode meta-steps x]
  (if (seq? x)
    (test-list msg-atom mode meta-steps x)
    (test-element msg-atom mode meta-steps x)))

(defn test-p-def
  ([mode p-def]
   (test-p-def mode p-def nil))
  ([mode p-def user-meta-steps]
   (let [meta-steps (distinct (concat default-meta-steps user-meta-steps))
         msg-atom (atom ())]
     (if (seq? p-def)
       (when (and (every?
                    identity
                    (doall (map (partial test-step msg-atom mode meta-steps) p-def)))
                  (= :failure mode))
         (report-fail msg-atom :success "Pipeline defintion is valid but you expected an error"))
       (report-fail msg-atom :succes "Pipeline definitions has to be a sequence"))
     (report-test msg-atom))))
