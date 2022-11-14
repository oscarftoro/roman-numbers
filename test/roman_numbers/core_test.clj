(ns roman-numbers.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [roman-numbers.core :refer :all]))


(deftest to-roman-test
  (testing "The conversion between integer i to roman-number and the opposite is i")
  (let [i (rand-int 4000)]
    (is (= i (roman-number->int (int->roman-number i))))))


