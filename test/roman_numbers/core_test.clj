(ns roman-numbers.core-test
  (:require [clojure.test :refer :all]
            [roman-numbers.core :refer :all]))


(deftest to-roman-test
  (testing "That the convertion between integer a-number to roman-number and the opposite is equivalent to a-number")
  (let [a-number (rand-int 4000)]
    (is (= a-number (roman-number->int (int->roman-number a-number))))))


