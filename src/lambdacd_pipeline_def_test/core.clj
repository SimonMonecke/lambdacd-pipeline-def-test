(ns lambdacd-pipeline-def-test.core
  (require [clojure.tools.logging :as log]))

(declare test-p-def)

(defn- test-list [l]
  (every?
    identity
    (doall (map test-p-def l))))

(defn- test-symbol [s]
  (let [r (boolean (resolve s))]
    (when (not r)
      (log/error s "is undefinied"))
    r))

(defn- test-element [x]
  (or (not (instance? clojure.lang.Symbol x))
      (test-symbol x)))

(defn test-p-def [x]
  (if (seq? x)
    (test-list x)
    (test-element x)))
