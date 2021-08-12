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
  (let [credit-cards-of-client (get client-data :client/credit-cards)
        filtered-card          (filter (fn [card]
                                         (= (get card :credit-card/number)
                                            (get credit-card :credit-card/number)))
                                       credit-cards-of-client)
        index-of-credit-card (.indexOf credit-cards-of-client (first filtered-card))]
    (if (not= index-of-credit-card -1)
      (update-in client-data [:client/credit-cards index-of-credit-card] (constantly credit-card))
      (update-in client-data [:client/credit-cards] conj credit-card))))

