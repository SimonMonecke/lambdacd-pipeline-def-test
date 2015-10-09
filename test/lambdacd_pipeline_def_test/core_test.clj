(ns lambdacd-pipeline-def-test.core-test
  (:require [clojure.test :refer :all]
            [lambdacd-pipeline-def-test.core :refer :all]))

(defn my-step1 [args ctx]
  {:status :success})

(defn my-step2 [args ctx]
  {:status :success})

(defn my-step4 [args ctx]
  {:status :success})

(defn my-step5 [env]
  (fn [args ctx]
    {:status :success}))

(deftest a-test
  (testing "pipeline with one definied step"
    (is (test-p-def `(my-step1))))
  (testing "pipeline with four definied steps"
    (is (test-p-def `(my-step1 my-step2 my-step4 my-step1))))
  (testing "pipeline with four steps, one is undefinied"
    (is (not (test-p-def `(my-step1 my-step2 my-step3 my-step1)))))
  (testing "pipeline with five definied steps, nested"
    (is (test-p-def `(my-step1 (my-step2 my-step4 my-step1) my-step1))))
  (testing "pipeline with four definied steps, one has a parameter"
    (is (test-p-def `(my-step1 my-step2 my-step4 (my-step5 "develop")))))
  (testing "pipeline with four definied steps, one has a parameter, nested"
    (is (test-p-def `(my-step1 my-step2 (my-step4 (my-step5 "develop"))))))
  (testing "pipeline with four definied steps, one has a parameter, double nested"
    (is (test-p-def `(my-step1 my-step2 (my-step1 (my-step4 (my-step5 "develop")))))))
  (testing "pipeline with four definied steps, one has a parameter, double nested 2"
    (is (test-p-def `((my-step1 my-step2) (my-step1 (my-step4 (my-step5 "develop")))))))
  (testing "pipeline with four steps, one is undefinied, nested"
    (is (not (test-p-def `(my-step1 (my-step2 my-step3) my-step1)))))
  (testing "pipeline with five steps, one is undefinied, double nested"
    (is (not (test-p-def `(my-step1 (my-step4 (my-step2 my-step3)) my-step1)))))
  (testing "pipeline with four steps, one is undefinied, double nested"
    (is (not (test-p-def `(my-step1 (my-step4 (my-step2 my-step3)) my-step1)))))
  (testing "pipeline with four steps, two are undefinied"
    (is (not (test-p-def `(my-step1 my-step3 my-step4 my-step6)))))
  (testing "pipeline with four steps, two are undefinied, nested"
    (is (not (test-p-def `(my-step1 my-step3 (my-step4 my-step6))))))
  (testing "pipeline with four steps, two are undefinied, double nested"
    (is (not (test-p-def `(my-step1 (my-step3 (my-step4 my-step6)))))))
  (testing "pipeline with five steps, two are undefinied, one defined has a parameter, double nested"
    (is (not (test-p-def `(my-step1 (my-step3 (my-step4 (my-step5 "develop") my-step6))))))))
