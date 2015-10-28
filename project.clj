(defproject lambdacd-pipeline-def-test "0.3.1"
  :description "A Clojure library designed to test whether all the steps in your LambdaCD pipeline definitions are definied."
  :url "https://github.com/SimonMonecke/lambdacd-pipeline-def-test"
  :license {:name "The MIT License (MIT)"
            :url "http://opensource.org/licenses/MIT"}
  :scm {:name "git"
        :url "https://github.com/SimonMonecke/lambdacd-pipeline-def-test.git"}
  :main lambdacd-pipeline-def-test.core
  :test-paths ["test"]
  :profiles {:uberjar {:aot :all}}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [lambdacd "0.5.5"]
                 [org.clojure/tools.logging "0.3.0"]])
