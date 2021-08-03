(ns credit-card.models.general
  (:require [schema.core :as s]))

(defn greater-or-equal-than-zero
  [x]
  (>= x 0))

(defn greater-than-one-and-less-or-equal-than-twelve
  [x]
  (and (>= x 1) (<= x 12)))

(def LocalDate java.time.LocalDate)
(def YearMonth java.time.YearMonth)

(def NumGreaterOrEqualThanZero
  (s/constrained s/Num greater-or-equal-than-zero 'greater-or-equal-than-zero))

(def Query
  {s/Keyword s/Any})

(def MonthValue (s/constrained s/Num greater-than-one-and-less-or-equal-than-twelve 'valid-month-value))
