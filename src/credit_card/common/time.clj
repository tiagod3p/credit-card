(ns credit-card.common.time)

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

(defn inst->local-date ^java.time.LocalDate
  [inst]
  (-> inst
      .toInstant
      (java.time.LocalDate/ofInstant java.time.ZoneOffset/UTC)))

(defn inst->year-month ^java.time.YearMonth
  [inst]
  (-> inst
      inst->local-date
      java.time.YearMonth/from))

