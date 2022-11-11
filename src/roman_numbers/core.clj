(ns roman-numbers.core
  (:require [clojure.spec.alpha :as s]
            [clojure.core.reducers :as r])
  (:gen-class))

; to validate
(def roman-number-regex #"^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})")

; to parse (we group up repeated M, X, C, or I)
(def roman-number-parser-regex #"^(M?)(M?)(M?)(CM|CD|D?)(C?)(C?)(C?)(XC|XL|L?)(X?)(X?)(X?)(IX|IV|V?)(I?)(I?)(I?)")

(s/def ::roman-number (s/and string? #(re-matches roman-number-regex %)))
(s/def ::roman-unit #{"I" "IV" "V" "IX" "X" "XL" "L" "XC" "C" "CD" "D" "CM" "M"})
(s/def ::roman-numeral #{\I \V \X \L \C \D \M})
(s/def ::previous char?)
(s/def ::current char?)
(s/def ::acc pos-int?)
(s/def ::counter #(and (fn [counter] (> counter 0))         ;counter checks that a numeral only repeats 3 times maximum
                       (fn [counter] (<= counter 3))))
(s/def ::integer (s/and pos-int? #(<= 3999 %)))

(def roman-str->int {"I"  1                                 ; roman numerals and substracted cases
                     "IV" 4
                     "V"  5
                     "IX" 9
                     "X"  10
                     "XL" 40
                     "L"  50
                     "XC" 90
                     "C"  100
                     "CD" 400
                     "D"  500
                     "CM" 900
                     "M"  1000})

(def int->roman-str {1    "I"
                     2    "II"
                     3    "III"
                     4    "IV"
                     5    "V"
                     6    "VI"
                     7    "VII"
                     8    "VIII"
                     9    "IX"
                     10   "X"
                     20   "XX"
                     30   "XXX"
                     40   "XL"
                     50   "L"
                     60   "LX"
                     70   "LXX"
                     80   "LXXX"
                     90   "XC"
                     100  "C"
                     200  "CC"
                     300  "CCC"
                     400  "CD"
                     500  "D"
                     600  "DC"
                     700  "DCC"
                     800  "DCCC"
                     900  "CM"
                     1000 "M"
                     2000 "MM"
                     3000 "MMM"})

(s/fdef group
        :args (s/cat :roman-number string?)
        :ret vector?
        :fn #(string? (rand-nth (:ret %))))

(defn group [roman-number]
  {:pre  [(s/valid? ::roman-number roman-number)]
   :post [(s/valid? vector? %) (s/valid? string? (rand-nth %))]}
  (vec (re-matches roman-number-parser-regex roman-number)))

(defn roman-number->int [^String roman-number]
  (let [number-vec  (try (->> (group roman-number)
                              (drop 1)
                              vec)
                         (catch Exception e (prn "Invalid roman number" (:message e))))
        numbers-vec (vec (remove empty? number-vec))]
    (->> numbers-vec
         (r/map roman-str->int)
         (r/reduce +))))

;; a digit 1 has length 1; 10 has length 2; 100 -> 3 and 1000 -> 4
;; the amount of necessary drops in a list of units [1000 100 10 1]
(def length->unit {1 3                                      ; unary
                   2 2                                      ; decimal
                   3 1                                      ; hundreds
                   4 0})                                    ; thousands

(defn- digits
  [n]
  {:pre  (s/valid? pos-int? n)
   :post [(s/valid? vector? %) (s/valid? int? (rand-nth %))]}
  (->> n str (mapv (comp read-string str))))

(defn int->roman-number
  "Given an integer return a roman number in O(256)"
  [i]
  {:pre  (s/valid? ::integer i)
   :post (s/valid? ::roman-number %)}
  (let [unit  (length->unit (-> i str count))
        units (drop unit [1000 100 10 1])]
    (->> (digits i)
         (map vector units)                                 ; zip i with units
         (map (partial apply *))                            ; multiply units with digits
         (map int->roman-str)
         (apply str))))

(defn operation
  "Pseudo smart function that given a number or an integer
   between [1 - 3999] Returns a meaningful result. A number
   for a valid roman number and a string representing a roman number for
   a number"
  [input]
  (try
    (if (string? input)
      (roman-number->int input)
      (int->roman-number input))
    (catch AssertionError ae
      (str "Hov! the thing is that  " (.getMessage ae)))))

(defn -main
  "given a valid string representing a roman number
  between [1-3999], delivers a decimal representation"
  [& args]
  (try (operation args)
       (catch Exception e
         (prn "Invalid roman number" (e :message)))))