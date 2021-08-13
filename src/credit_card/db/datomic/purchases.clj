(ns credit-card.db.datomic.purchases
  (:require [credit-card.models.purchase :as models.purchase]
            [credit-card.db.datomic.credit-card :as db.credit-card]
            [credit-card.common.utils :as common.utils]
            [credit-card.common.time :as common.time]
            [datomic.api :as d]
            [schema.core :as s]))

(s/defn purchase->datomic-purchase
  [purchase :- models.purchase/Purchase]
  (-> purchase
      (common.utils/add-uuid "purchase")
      (update :purchase/date common.time/local-date->inst)
      (update :purchase/amount float)
      (update :purchase/credit-card (constantly [:credit-card/id (get-in purchase [:purchase/credit-card :credit-card/id])]))))

(s/defn datomic-purchase->purchase :- models.purchase/Purchase
  [datomic-purchase]
  (-> datomic-purchase
      common.utils/remove-all-db-ids
      (update :purchase/date common.time/inst->local-date)
      (update :purchase/credit-card db.credit-card/datomic-credit-card->credit-card)))

(s/defn upsert-purchase-and-update-credit-card!
  [purchase :- models.purchase/Purchase
   conn]
  (let [credit-card-datomic (db.credit-card/credit-card->datomic-credit-card (get purchase :purchase/credit-card))
        purchase-datomic (purchase->datomic-purchase purchase)]
    (d/transact conn [purchase-datomic credit-card-datomic])))

(s/defn purchases-by-credit-card-id
  [credit-card-id :- java.util.UUID db]
  (d/q '[:find [(pull ?purchase [* {:purchase/credit-card [*]}]) ...]
         :in $ ?credit-card-id
         :where [?purchase :purchase/credit-card ?credit-card]
         [?credit-card :credit-card/id ?credit-card-id]]
       db credit-card-id))
