(ns credit-card.logic.credit-card
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]
            [credit-card.models.credit-card :as models.credit-card]))

(s/defn new-limit :- models.general/NumGreaterOrEqualThanZero
  [limit        :- models.general/NumGreaterOrEqualThanZero
   amount       :- models.general/NumGreaterOrEqualThanZero]
  (- limit amount))

(s/defn associate-credit-card-with-client :- models.credit-card/ClientData 
  [credit-card :- models.credit-card/CreditCard
   client-data :- models.credit-card/ClientData]
  (assoc client-data :credit-card credit-card))
