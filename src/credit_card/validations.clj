(ns credit-card.validations)

(defn expired-card?
  [expiration-date purchase-date]
  (let [purchase-date-year-and-month (java.time.YearMonth/from purchase-date)
        chrono-months (java.time.temporal.ChronoUnit/MONTHS)
        diff-in-months (.until purchase-date-year-and-month expiration-date chrono-months)]
    (<= diff-in-months 0)))

(defn limit?
  [limit amount]
  (>= limit amount))

(defn valid-purchase?
  [limit? expired-card?]
  (and limit? (not expired-card?)))

(defn new-limit
  [limit amount]
  (- limit amount))
