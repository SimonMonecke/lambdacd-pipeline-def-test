(ns lambdacd-pipeline-def-test.core-test
  (:require [clojure.test :refer :all]
            [lambdacd-pipeline-def-test.core :refer :all]))

(defn my-step1 [args ctx]
  {:status :success})

(defn my-step2 [args ctx]
  {:status :success})

(defn my-step4 [args ctx]
  {:status :success})

(defn my-step-one-arg
  {:meta-step true}
  [s1]
  (fn [args ctx]
    {:status :success}))

(defn my-step-two-args
  [s1 s2]
  (fn [args ctx]
    {:status :success}))

(defn my-step-two-or-three-args
  {:meta-step true}
  ([s1 s2]
   (my-step-two-or-three-args s1 s2 :something))
  ([s1 s2 s3]
   (fn [args ctx]
     {:status :success})))

(defn my-step-var-args
  {:meta-step true}
  [s1 & s2]
  (fn [args ctx]
    {:status :success}))

(deftest test-p-def-test
  (testing "pipeline with one definied step"
    (test-p-def :success `(my-step1)))
  (testing "pipeline with four definied steps"
    (test-p-def :success `(my-step1 my-step2 my-step4 my-step1)))
  (testing "pipeline with four steps, one is undefinied"
    (test-p-def :failure `(my-step1 my-step2 my-step3 my-step1)))
  (testing "pipeline with five definied steps, nested, meta-step flag as third argument"
    (test-p-def :success `(my-step1 (my-step-two-args my-step4 my-step1) my-step1) `(my-step-two-args)))
  (testing "pipeline with four definied steps, one has a parameter"
    (test-p-def :success `(my-step1 my-step2 my-step4 (my-step-one-arg "develop"))))
  (testing "pipeline with four definied steps, one has a parameter, nested"
    (test-p-def :success `(my-step1 my-step2 (my-step-one-arg (my-step-one-arg "develop")))))
  (testing "pipeline with four definied steps, one has a parameter, double nested"
    (test-p-def :success `(my-step1 my-step2 (my-step-one-arg (my-step-one-arg (my-step-one-arg "develop"))))))
  (testing "pipeline with four definied steps, one has a parameter, double nested 2"
    (test-p-def :success `((my-step-one-arg my-step2) (my-step-one-arg (my-step-one-arg (my-step-one-arg "develop"))))))
  (testing "pipeline with four steps, one is undefinied, nested"
    (test-p-def :failure `(my-step1 (my-step-one-arg my-step3) my-step1)))
  (testing "pipeline with five steps, one is undefinied, double nested"
    (test-p-def :failure `(my-step1 (my-step-one-arg (my-step2 my-step3)) my-step1)))
  (testing "pipeline with four steps, one is undefinied, double nested"
    (test-p-def :failure `(my-step1 (my-step-one-arg (my-step2 my-step3)) my-step1)))
  (testing "pipeline with four steps, two are undefinied"
    (test-p-def :failure `(my-step1 my-step3 my-step4 my-step6)))
  (testing "pipeline with four steps, two are undefinied, nested"
    (test-p-def :failure `(my-step1 my-step3 (my-step-one-arg my-step6))))
  (testing "pipeline with four steps, two are undefinied, double nested"
    (test-p-def :failure `(my-step1 (my-step3 (my-step-one-arg my-step6)))))
  (testing "pipeline with five steps, one are undefinied, one defined has a parameter, double nested"
    (test-p-def :failure `(my-step1 (my-step-one-arg (my-step-two-args (my-step-one-arg "develop") my-step6)))))
  (testing "pipeline three steps, two or three args, two"
    (test-p-def :success `(my-step1 (my-step-two-or-three-args "dev" 5) my-step4)))
  (testing "pipeline three steps, two or three args, three"
    (test-p-def :success `(my-step1 (my-step-two-or-three-args "dev" 5 1) my-step4)))
  (testing "pipeline three steps, two or three args, four invalid"
    (test-p-def :failure `(my-step1 (my-step-two-or-three-args "dev" 5 1 5) my-step4)))
  (testing "pipeline three steps, two or three args, one invalid"
    (test-p-def :failure `(my-step1 (my-step-two-or-three-args "dev") my-step4)))
  (testing "pipeline three steps, var args, one valid"
    (test-p-def :success `(my-step1 (my-step-var-args "dev") my-step4)))
  (testing "pipeline three steps, var args, two valid"
    (test-p-def :succes `(my-step1 (my-step-var-args "dev" 5) my-step4)))
  (testing "pipeline three steps, var args, zero invalid"
    (test-p-def :failure `(my-step1 my-step-var-args my-step4))))
