(ns lambdacd-pipeline-def-test.core
  (require [clojure.tools.logging :as log]))

(defn test-p-def [x]
  (if (seq? x)
    (every? identity (doall (map test-p-def x)))
    (let [r (boolean (resolve x))]
      (when (not r)
        (log/error x "is undefinied"))
      r)))
