(ns roman-numbers.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

;; REGEXES
;; to validate
(def roman-number-regex #"^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})")

;; to parse (we group up repeated M, X, C, or I)
(def roman-number-parser-regex #"^(M?)(M?)(M?)(CM|CD|D?)(C?)(C?)(C?)(XC|XL|L?)(X?)(X?)(X?)(IX|IV|V?)(I?)(I?)(I?)")

(s/def ::roman-number-valid (s/and string? #(re-matches roman-number-regex %)))

;; combinations
;; how can we help the generator without interfering shrinking?
;; ...
;; what about a series of specialized generators?
(def roman-units #{"I" "II" "III" "IV" "V" "VI" "VII" "VIII" "IX"})
(def roman-tens #{"X" "XX" "XXX" "XL" "L" "LX" "LXX" "LXXX" "XC"})
(def roman-hundreds #{"C" "CC" "CCC" "CD" "D" "DC" "DCC" "DCCC" "CM"})
(def roman-thousand-units #{"M" "MM" "MMM"})

(def roman-units-gen (gen/such-that #(not= % empty?) (gen/elements roman-units)))
(def roman-tens-gen (gen/such-that #(not= % empty?) (gen/elements roman-tens)))
(def roman-hundreds-gen (gen/such-that #(not= % empty?) (gen/elements roman-hundreds)))
(def roman-thousand-units-gen (gen/such-that #(not= % empty?) (gen/elements roman-thousand-units)))

(def tens-units-gen
  (gen/fmap (partial apply str) (gen/tuple roman-tens-gen roman-units-gen)))

(def hundreds-units-gen
  (gen/fmap (partial apply str) (gen/tuple roman-hundreds-gen roman-units-gen)))

(def hundreds-tens-gen
  (gen/fmap (partial apply str) (gen/tuple roman-hundreds-gen roman-tens-gen)))

(def thousand-units-units-gen
  (gen/fmap (partial apply str) (gen/tuple roman-thousand-units-gen roman-units-gen)))

(def thousand-units-tens-units-gen
  (gen/fmap (partial apply str) (gen/tuple roman-thousand-units-gen roman-tens-gen roman-units-gen)))

(def thousand-units-hundreds-gen
  (gen/fmap (partial apply str) (gen/tuple roman-thousand-units-gen roman-hundreds-gen)))

(def thousand-units-hundreds-tens-gen
  (gen/fmap (partial apply str) (gen/tuple roman-thousand-units-gen roman-hundreds-gen roman-tens-gen)))

(def thousand-units-hundreds-tens-units-gen
  (gen/fmap (partial apply str) (gen/tuple roman-thousand-units-gen roman-hundreds-gen roman-tens-gen roman-units-gen)))

(s/def ::roman-number (s/with-gen ::roman-number-valid #(gen/one-of [roman-units-gen
                                                                     roman-tens-gen
                                                                     roman-hundreds-gen
                                                                     roman-thousand-units-gen
                                                                     tens-units-gen
                                                                     hundreds-units-gen
                                                                     hundreds-tens-gen
                                                                     thousand-units-units-gen
                                                                     thousand-units-tens-units-gen
                                                                     thousand-units-hundreds-gen
                                                                     thousand-units-hundreds-tens-gen
                                                                     thousand-units-hundreds-tens-units-gen])))

(s/def ::integer (s/and pos-int? #(<= 3999 %)))
;; the return of a digit 1994 is [1 9 9 4]
(s/def ::digit-return (s/and vector? ::integer))            ; < 3999