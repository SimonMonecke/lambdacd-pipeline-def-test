# lambdacd-pipeline-def-test

If you define your pipeline definition in LambdaCD it could happen that you have a typo in a step name or that you forget to import a namespace. In this case you are able to build and run your pipeline but it will stop if it tries to execute this undefinied step. And it will stop without any exception.

This library can be used in your test namespace to avoid this behaviour.

Another feature of this library is to test the argument lists of your meta-steps. The test will fail if you try to use a regular LambdaCD step as meta-step or if you provide the wrong count of arguments.

## Usage

```clojure
[lambdacd-pipeline-def-test.core :refer :all]

(deftest pipeline-test
  (testing "test that all steps are definied in your pipeline"
    (is (test-p-def :success pipeline-def)))

test-p-def [mode p-def user-meta-steps]
```

* mode: :success or :failure
* p-def: Your pipeline definition
* user-meta-steps: List of meta-steps that don't be declared as meta-step even though they are.

## Meta-Steps

A meta-step is step in your pipeline which creates a regular LambdaCD step.

Example:
```clojure

(defn deploy-my-app [env]
  (fn [args ctx]
    ;deploy to env
    {:status ...}))

(def pipeline-def
  `(step-1
    step-2
    (deploy-my-app "dev")
    step-3
    (deploy-my-app "live"))
```
By using a meta-step you can parameterize your steps to use them in different stages of your pipeline.
Regular LambdaCD steps also take two arguments: args and ctx. They will be injected when you run your pipeline.
To validate your pipeline definition this library has to make a distinction between these two kinds of steps. There are two ways how you can tag your step as a meta-step:

### Add the :meta-step tag to the function declaration:
```clojure

(defn deploy-my-app
  {:meta-step true}
  [env]
  (fn [args ctx]
    ;deploy to env
    {:status ...}))
```

### Pass a list with all meta-steps to the test-p-def function
```clojure

(defn deploy-my-app [env]
  (fn [args ctx]
    ;deploy to env
    {:status ...}))

(def pipeline-def
  ...)

(deftest pipeline-test
  (testing "test that all steps are definied in your pipeline"
    (is (test-p-def :success pipeline-def `(deploy-my-app))))
```
You can use this way if can not change the function declaration.
## License

Copyright © 2015 Simon Monecke

Distributed under MIT License
