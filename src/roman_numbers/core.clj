(ns roman-numbers.core
  (:require [clojure.spec.alpha :as s]
            [clojure.core.reducers :as r]
            [roman-numbers.specs :as specs])
  (:gen-class))

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

(defn group
  "Given a roman number, returns a vector of roman units, tens,
   hundreds and/or thousands. Order the values to easily
   apply reduce with roman-str->int and +.
   For example:
   'MMXIX' -> ['MMXIX'  'M'  'M' '' '' '' '' '' '' 'X' '' ''  'IX' '' '' '']"
  [roman-number]
  {:pre  [(s/valid? ::specs/roman-number roman-number)]
   :post [(s/valid? vector? %) (s/valid? string? (rand-nth %))]}
  (vec (re-matches specs/roman-number-parser-regex roman-number)))

(s/fdef roman-number->int
        :args (s/cat :roman-number string?)
        :ret int?)

(defn roman-number->int [^String roman-number]
  (let [number-vec  (->> (group roman-number)
                         (drop 1)
                         vec)
        numbers-vec (vec (remove empty? number-vec))]
    (->> numbers-vec
         (r/map roman-str->int)
         (r/reduce +))))

;; a digit 1 has length 1; 10 has length 2; 100 -> 3 ...
;; the amount of necessary drops in a list of place values [1000 100 10 1]
(def length->drop-amount {1 3                               ; unary
                          2 2                               ; decimal
                          3 1                               ; hundreds
                          4 0})                             ; thousands

(s/fdef digits
        :args (s/cat :n ::specs/integer)
        :ret ::specs/digit-return)

(defn- digits
  "Takes a number and returns a vector with digits. For instance
   1991 -> [1 9 9 1]"
  [n]
  {:pre  (s/valid? pos-int? n)
   :post [(s/valid? vector? %) (s/valid? int? (rand-nth %))]}
  (->> n str (mapv (comp read-string str))))

(defn int->roman-number
  "Given an integer return a roman number in O(256)"
  [i]
  {:pre  (s/valid? ::specs/integer i)
   :post (s/valid? ::specs/roman-number %)}
  (let [drop-amount (length->drop-amount (-> i str count))
        units       (drop drop-amount [1000 100 10 1])]
    (->> (digits i)
         (map vector units)                                 ; zip i with units
         (map (partial apply *))                            ; multiply units with digits
         (map int->roman-str)
         (apply str))))

(defn operation
  "Given a roman number or an integer
   between [1 - 3999], returns a meaningful result. A number
   for a valid roman number and a string representing a roman number for
   an integer [1 - 3999]. Inputs 0 and n > 4000 returns an empty string."
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
  (try
    (let [first-arg       (first args)
          is-it-a-number? (number? (try (Long/parseLong first-arg) ; number or false!! :??-(
                                        (catch NumberFormatException _e false)))]
      (if is-it-a-number? (-> first-arg
                              Long/parseLong
                              operation
                              prn)
                          (-> first-arg
                              operation
                              prn)))
    (catch Exception _e
      (prn (str (first args) " is not a valid number. Try with a number between 1 and 3999")))))
