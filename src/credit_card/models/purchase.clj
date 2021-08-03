(ns credit-card.models.purchase
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]
            [credit-card.models.credit-card :as models.credit-card]))

(def Purchase
  {:date        models.general/LocalDate
   :amount      models.general/NumGreaterOrEqualThanZero
   :merchant    s/Str
   :category    s/Str
   :approved?   s/Bool
   :credit-card models.credit-card/CreditCard})

(def CategoryAmount
  {s/Str models.general/NumGreaterOrEqualThanZero})
