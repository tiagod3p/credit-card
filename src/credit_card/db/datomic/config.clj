(ns credit-card.db.datomic.config
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/fintech")

(defn open-connection!
  [db-uri]
  (d/create-database db-uri)
  (d/connect db-uri))

(defn delete-db!
  [db-uri]
  (d/delete-database db-uri))

(defn create-schema!
  [conn schema]
  (d/transact conn schema))



