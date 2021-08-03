(ns credit-card.logic.validations
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]))

(s/defn expired-card? :- s/Bool
  [expiration-date    :- models.general/YearMonth
   purchase-date      :- models.general/LocalDate]
  (let [purchase-date-year-and-month (java.time.YearMonth/from purchase-date)
        chrono-months (java.time.temporal.ChronoUnit/MONTHS)
        diff-in-months (.until purchase-date-year-and-month expiration-date chrono-months)]
    (<= diff-in-months 0)))

(s/defn limit? :- s/Bool
  [limit       :- models.general/NumGreaterOrEqualThanZero
   amount      :- models.general/NumGreaterOrEqualThanZero]
  (>= limit amount))

(s/defn valid-purchase? :- s/Bool
  [limit?               :- s/Bool
   expired-card?        :- s/Bool]
  (and limit? (not expired-card?)))
