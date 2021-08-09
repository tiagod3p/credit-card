(ns credit-card.generators
  (:require [clojure.test.check.generators :as gen]
            [schema-generators.generators :as g]))

(def date-gen
  "Date generator logic."
  (gen/let [y (gen/choose 2010 2100)
            m (gen/choose 1 12)
            d (gen/choose 1 31)]
    (try
      (java.time.LocalDate/of y m d)
      (catch java.time.DateTimeException _
        ;; easy way out if we generate an invalid date
        ;; like 2020-02-30
        (gen/let [safe-d (gen/choose 1 28)]
          (java.time.LocalDate/of y m safe-d))))))

(def date
  "Generates a date between 2010-01-01 and 2100-12-31."
  (gen/elements (gen/sample date-gen 25)))

(def year-month
  "Generates a date between 2010-01-01 and 2100-12-31 in the format year-month."
  (gen/fmap #(java.time.YearMonth/from %) date))

(def leaf-generators
  "Leaf Generators to generate specific schemas."
  {java.time.YearMonth year-month
   java.time.LocalDate date})

(defn generate
  "Generate schemas using leaf generators."
  [schema]
  (g/generate schema leaf-generators))
