(ns roman-numbers.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as str]))

; to validate
(def roman-number-regex #"^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})")

; from https://www.oreilly.com/library/view/regular-expressions-cookbook/9780596802837/ch06s09.html
(def rno #"^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$")
; to parse (we group up repeated M, X, C, or I)
(def roman-number-parser-regex #"^(M?)(M?)(M?)(CM|CD|D?)(C?)(C?)(C?)(XC|XL|L?)(X?)(X?)(X?)(IX|IV|V?)(I?)(I?)(I?)")

; some specs
(s/def ::roman-number-valid (s/and string? #(re-matches roman-number-regex %)))

(def roman-units #{"I" "II" "III" "IV" "V" "VI" "VII" "VIII" "IX"})
(s/def ::units roman-units)

(def roman-tens #{"X" "XX" "XXX" "XL" "L" "LX" "LXX" "LXXX" "XC"})
(s/def ::tens roman-tens)

(def roman-hundreds #{"C" "CC" "CCC" "CD" "D" "DC" "DCC" "DCCC" "CM"})
(s/def ::hundreds roman-hundreds)

(def roman-thousand-units #{"M" "MM" "MMM"})
(s/def ::thousand-unit roman-thousand-units)

; combinations
; how can we help the generator without interfering shrinking?
(s/def ::tens-units (s/and string?
                           #(str/starts-with? (roman-tens %) %)
                           #(str/ends-with? (roman-units %) %)))

(defn roman-numeral-builder
  ([this that] (str ((comp rand-nth vec) this)
                    ((comp rand-nth vec) that)))
  ([this that and-that] (str ((comp rand-nth vec) this)
                             ((comp rand-nth vec) that)
                             ((comp rand-nth vec) and-that))))

(defn tens-units [] (roman-numeral-builder roman-tens roman-units))

(defn hundreds-units [] (roman-numeral-builder roman-hundreds roman-units))
(defn hundreds-tens-unit [] (roman-numeral-builder roman-hundreds roman-tens roman-units))

(defn thousand-units-units [] (roman-numeral-builder roman-thousand-units roman-units))
(defn thousand-units-hundreds-tens [] (roman-numeral-builder roman-thousand-units roman-hundreds roman-tens))
(defn thousand-units-hundreds [] (roman-numeral-builder roman-thousand-units roman-hundreds))
(defn thousand-units-hundreds-tens-units []
  (str ((comp rand-nth vec) roman-thousand-units)
       hundreds-tens-unit))

(s/def ::combinations (s/and string? ::roman-number-valid))
(s/def ::roman-number (s/or ::units ::tens ::hundreds ::thousand-unit))