(ns roman-numbers.core-test
  (:require
    [clojure.spec.alpha :as s]
    [clojure.test :refer :all]
    [clojure.test.check :as tc]
    [clojure.test.check.properties :as prop]
    [roman-numbers.specs :as specs]
    [roman-numbers.core :refer :all]))

; as a unit-test
(deftest to-roman-test
  (testing "The conversion between integer i to roman-number and the opposite is i")
  (let [i (rand-int 4000)]
    (is (= i (roman-number->int (int->roman-number i))))))


;; as a quickcheck property
(def a-randomly-generated-roman-number-is-equivalent-to-applying-operation-fun-twice-to-it
  (prop/for-all [roman-numeral  (s/gen ::specs/roman-number)]
                (let [hindu-arabic (operation roman-numeral)
                      our-roman-numeral (operation hindu-arabic)]
                  (= roman-numeral our-roman-numeral))))

(tc/quick-check 6000 a-randomly-generated-roman-number-is-equivalent-to-applying-operation-fun-twice-to-it)

(comment
  (tc/quick-check 1000000 a-randomly-generated-roman-number-is-equivalent-to-applying-operation-fun-twice-to-it)
  )


