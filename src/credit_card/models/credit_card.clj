(ns credit-card.models.credit-card
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]))

(s/def CreditCard
  {:number          s/Str
   :cvv             s/Str
   :expiration-date models.general/YearMonth
   :limit           models.general/NumGreaterOrEqualThanZero})

(def ClientData
  {:full-name   s/Str
   :cpf         s/Str
   :email       s/Str
   :credit-cards [CreditCard]})
