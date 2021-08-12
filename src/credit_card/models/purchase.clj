(ns credit-card.models.purchase
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]
            [credit-card.models.credit-card :as models.credit-card]))

(def Purchase
  {:purchase/date        models.general/LocalDate
   :purchase/amount      models.general/NumGreaterOrEqualThanZero
   :purchase/merchant    s/Str
   :purchase/category    s/Str
   :purchase/approved?   s/Bool
   :purchase/credit-card models.credit-card/CreditCard})

(def CategoryAmount
  {s/Str models.general/NumGreaterOrEqualThanZero})

(def datomic-schemas [{:db/ident       :purchase/id
                       :db/valueType   :db.type/uuid
                       :db/cardinality :db.cardinality/one
                       :db/unique      :db.unique/identity}
                      {:db/ident       :purchase/date
                       :db/valueType   :db.type/instant
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :purchase/amount
                       :db/valueType   :db.type/float
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :purchase/merchant
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :purchase/category
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :purchase/approved?
                       :db/valueType   :db.type/boolean
                       :db/cardinality :db.cardinality/many}
                      {:db/ident       :purchase/credit-card
                       :db/valueType   :db.type/ref
                       :db/cardinality :db.cardinality/one}])
