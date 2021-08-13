(ns credit-card.db.datomic.credit-card
  (:require [credit-card.models.credit-card :as models.credit-card]
            [credit-card.common.utils :as common.utils]
            [credit-card.common.time :as common.time]
            [datomic.api :as d]
            [schema.core :as s]))

(s/defn client-data->datomic-client-data
  [client-data :- models.credit-card/ClientData]
  (common.utils/add-uuid client-data "client"))

(s/defn datomic-credit-card->credit-card :- models.credit-card/CreditCard
  [datomic-credit-card]
  (-> datomic-credit-card
      common.utils/remove-all-db-ids
      (update :credit-card/expiration-date common.time/inst->year-month)))

(s/defn credit-card->datomic-credit-card
  [credit-card :- models.credit-card/CreditCard]
  (-> credit-card
      (common.utils/add-uuid "credit-card")
      (update :credit-card/expiration-date common.time/year-month->inst)
      (update :credit-card/limit float)))

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

(s/defn all-credit-cards
  [db]
  (d/q '[:find [(pull ?credit-card [*]) ...]
         :where [?credit-card :credit-card/id _]] db))

(s/defn credit-card-by-id
  [credit-card-id :- java.util.UUID db]
  (d/pull db '[*] [:credit-card/id credit-card-id]))
