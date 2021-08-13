(ns credit-card.db.datomic.credit-card
  (:require [credit-card.models.purchase :as models.purchase]
            [credit-card.models.credit-card :as models.credit-card]
            [credit-card.common.utils :as common.utils]
            [credit-card.common.time :as common.time]
            [datomic.api :as d]
            [schema.core :as s]))

(defn add-uuid
  ([m]
   (if (get m :id)
     m
     (assoc m :id (common.utils/uuid))))

  ([m namespace]
   (let [ns-keyword (keyword namespace "id")]
     (if (get m ns-keyword)
       m
       (assoc m ns-keyword (common.utils/uuid))))))

(s/defn client-data->datomic-client-data
  [client-data :- models.credit-card/ClientData]
  (add-uuid client-data "client"))

(s/defn credit-card->datomic-credit-card
  [credit-card :- models.credit-card/CreditCard]
  (-> credit-card
      (add-uuid "credit-card")
      (update :credit-card/expiration-date common.time/year-month->inst)
      (update :credit-card/limit float)))

(s/defn purchase->datomic-purchase
  [purchase :- models.purchase/Purchase]
  (-> purchase
      (add-uuid "purchase")
      (update :purchase/date common.time/local-date->inst)
      (update :purchase/amount float)
      (update :purchase/credit-card (constantly [:credit-card/id (get-in purchase [:purchase/credit-card :credit-card/id])]))))

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

(s/defn purchases-by-credit-card-id
  [credit-card-id :- java.util.UUID db]
  (d/q '[:find [(pull ?purchase [*]) ...]
         :in $ ?credit-card-id
         :where [?purchase :purchase/credit-card ?credit-card]
         [?credit-card :credit-card/id ?credit-card-id]]
       db credit-card-id))

(s/defn all-credit-cards
  [db]
  (d/q '[:find [(pull ?credit-card [*]) ...]
         :where [?credit-card :credit-card/id _]] db))

(s/defn credit-card-by-id
  [credit-card-id :- java.util.UUID db]
  (d/pull db '[*] [:credit-card/id credit-card-id]))
