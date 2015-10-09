# lambdacd-pipeline-def-test

If you define you definie yout pipeline definition in LambdaCD it could happen that you have a typo in a step name or that you forget to import a namespace. In this case you are able to build and run your pipeline but it will stop if it tries to execute this undefinied step. And it will stop without any exeception.

This library can be used in your test namespace to avoid this behaviour.

## Usage

```clojure
[lambdacd-pipeline-def-test.core :refer :all]

(deftest pipeline-test
  (testing "test all steps are definied in your pipeline"
    (is (test-p-def pipeline-def)))
```

## License

Copyright © 2015 Simon Monecke

Distributed under MIT License
