(ns credit-card.models.credit-card
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]))

(def CreditCard
  {:number          s/Str
   :cvv             s/Str
   :expiration-date models.general/YearMonth
   :limit           models.general/NumGreaterOrEqualThanZero})
