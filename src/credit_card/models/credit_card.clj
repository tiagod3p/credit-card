(ns credit-card.models.credit-card
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]))

(s/def CreditCard
  {:credit-card/id              java.util.UUID
   :credit-card/number          s/Str
   :credit-card/cvv             s/Str
   :credit-card/expiration-date models.general/YearMonth
   :credit-card/limit           models.general/NumGreaterOrEqualThanZero})

(s/def ClientData
  {:client/full-name    s/Str
   :client/cpf          s/Str
   :client/email        s/Str
   :client/credit-cards [CreditCard]})

(def datomic-schemas [{:db/ident       :client/id
                       :db/valueType   :db.type/uuid
                       :db/cardinality :db.cardinality/one
                       :db/unique      :db.unique/identity}
                      {:db/ident       :client/full-name
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :client/cpf
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :client/email
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :client/credit-cards
                       :db/valueType   :db.type/ref
                       :db/cardinality :db.cardinality/many}

                      {:db/ident       :credit-card/id
                       :db/valueType   :db.type/uuid
                       :db/cardinality :db.cardinality/one
                       :db/unique      :db.unique/identity}
                      {:db/ident       :credit-card/number
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :credit-card/cvv
                       :db/valueType   :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :credit-card/expiration-date
                       :db/valueType   :db.type/instant
                       :db/cardinality :db.cardinality/one}
                      {:db/ident       :credit-card/limit
                       :db/valueType   :db.type/float
                       :db/cardinality :db.cardinality/one}])


