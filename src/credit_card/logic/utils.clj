(ns credit-card.logic.utils)

(defn local-date-time->inst
  [^java.time.LocalDateTime local-date-time]
  (-> local-date-time
      (.toInstant java.time.ZoneOffset/UTC)
      .toEpochMilli java.util.Date.))

(defn local-date->inst
  [^java.time.LocalDate local-date]
  (-> local-date
      .atStartOfDay
      local-date-time->inst))

(defn year-month->inst
  [^java.time.YearMonth ym]
  (local-date->inst (java.time.LocalDate/of (.getYear ym) (.getMonth ym) 1)))

(defn uuid
  []
  (java.util.UUID/randomUUID))

