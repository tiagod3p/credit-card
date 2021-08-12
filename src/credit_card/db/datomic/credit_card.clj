(ns credit-card.db.datomic.credit-card
  (:require [credit-card.models.purchase :as models.purchase]
            [credit-card.models.credit-card :as models.credit-card]
            [credit-card.logic.utils :as logic.utils]
            [datomic.api :as d]
            [schema.core :as s]))

(s/defn client-data->datomic-client-data
  [client-data :- models.credit-card/ClientData]
  (if (get client-data :client/id)
    client-data
    (assoc client-data :client/id (logic.utils/uuid))))

(s/defn credit-card->datomic-credit-card
  [credit-card :- models.credit-card/CreditCard]
  (let [credit-card-updated (-> credit-card
                                (update :credit-card/expiration-date logic.utils/year-month->inst)
                                (update :credit-card/limit float))]
    (if (get credit-card-updated :credit-card/id)
      credit-card-updated
      (assoc credit-card-updated :credit-card/id (logic.utils/uuid)))))

(s/defn purchase->datomic-purchase
  [purchase :- models.purchase/Purchase]
  (let [purchase-updated (-> purchase
                             (update :purchase/date logic.utils/local-date->inst)
                             (update :purchase/amount float)
                             (update
                              :purchase/credit-card
                              (constantly [:credit-card/id (get-in purchase [:purchase/credit-card :credit-card/id])])))]
    (if (get purchase-updated :purchase/id)
      purchase-updated
      (assoc purchase-updated :purchase/id (logic.utils/uuid)))))

(s/defn upsert-client-data!
  [client-data :- models.credit-card/ClientData
   conn]
  (let [client-data-datomic (client-data->datomic-client-data client-data)]
    (d/transact conn [client-data-datomic])))

(s/defn upsert-credit-card!
  [credit-card :- models.credit-card/CreditCard
   conn]
  (let [credit-card-datomic (credit-card->datomic-credit-card credit-card)]
    (d/transact conn [credit-card-datomic])))

(s/defn upsert-purchase-and-update-credit-card!
  [purchase :- models.purchase/Purchase
   conn]
  (let [credit-card-datomic (credit-card->datomic-credit-card (get purchase :purchase/credit-card))
        purchase-datomic (purchase->datomic-purchase purchase)]
    (d/transact conn [purchase-datomic credit-card-datomic])))

(s/defn all-credit-cards
  [db]
  (d/q '[:find [(pull ?credit-card [*]) ...]
         :where [?credit-card :credit-card/id _]] db))

(s/defn credit-card-by-id
  [credit-card-id :- java.util.UUID db]
  (d/pull db '[*] [:credit-card/id credit-card-id]))
