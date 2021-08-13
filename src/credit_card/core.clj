(ns credit-card.core
  (:require [credit-card.logic.purchases :as logic.purchases]
            [credit-card.logic.credit-card :as logic.credit-card]
            [credit-card.common.utils :as common.utils]
            [credit-card.models.credit-card :as models.credit-card]
            [credit-card.models.purchase :as models.purchase]
            [credit-card.db.datomic.config :as db.config]
            [credit-card.db.datomic.credit-card :as db.credit-card]
            [datomic.api :as d]))

(def client-data {:client/full-name    "Tiago Vidal"
                  :client/cpf          "999.999.999-99"
                  :client/email        "tiago@my-email.com.br"
                  :client/credit-cards []})

(def credit-card {:credit-card/id              (common.utils/uuid)
                  :credit-card/number          "111"
                  :credit-card/cvv             "222"
                  :credit-card/expiration-date (java.time.YearMonth/parse "2029-09")
                  :credit-card/limit           1000})

(def credit-card-black {:credit-card/id              (common.utils/uuid)
                        :credit-card/number          "1000 2000 3000 0545"
                        :credit-card/cvv             "901"
                        :credit-card/expiration-date (java.time.YearMonth/parse "2029-09")
                        :credit-card/limit           50000})

(db.config/delete-db! db.config/db-uri)
(def conn (db.config/open-connection! db.config/db-uri))

(db.config/create-schema! conn (concat
                                models.credit-card/datomic-schemas
                                models.purchase/datomic-schemas))

(db.credit-card/upsert-client-data! client-data conn)

(db.credit-card/upsert-credit-card! credit-card conn)
(db.credit-card/upsert-credit-card! credit-card-black conn)

(db.credit-card/all-credit-cards (d/db conn))

(def purchase-1 (logic.purchases/purchase
                 (java.time.LocalDate/parse "2021-05-07")
                 199
                 "iFood"
                 "Restaurant"
                 credit-card))

(def purchase-2 (logic.purchases/purchase
                 (java.time.LocalDate/parse "2021-10-07")
                 95
                 "C&A"
                 "Clothing"
                 (:purchase/credit-card purchase-1)))

(def purchase-3 (logic.purchases/purchase
                 (java.time.LocalDate/parse "2021-10-07")
                 100
                 "C&A"
                 "Clothing"
                 (:purchase/credit-card purchase-2)))

(def purchase-4 (logic.purchases/purchase
                 (java.time.LocalDate/parse "2021-11-22")
                 23350
                 "Burger King"
                 "Restaurant"
                 credit-card-black))

(def all-purchases [purchase-1 purchase-2 purchase-3 purchase-4])

(mapv #(db.credit-card/upsert-purchase-and-update-credit-card! % conn) all-purchases)

(db.credit-card/credit-card-by-id (:credit-card/id credit-card) (d/db conn))
(db.credit-card/credit-card-by-id (:credit-card/id credit-card-black) (d/db conn))

(db.credit-card/purchases-by-credit-card-id (:credit-card/id credit-card) (d/db conn))
(db.credit-card/purchases-by-credit-card-id (:credit-card/id credit-card-black) (d/db conn))

(def approved-purchases (logic.purchases/search-purchases all-purchases {:purchase/approved? true}))

(def client-data-with-initial-credit-card-associated (logic.credit-card/associate-credit-card-with-client credit-card client-data))

(def credit-card-most-updated (:purchase/credit-card (last all-purchases)))

(def client-data-updated (logic.credit-card/associate-credit-card-with-client
                          credit-card-most-updated
                          client-data-with-initial-credit-card-associated))

(def purchases-client (logic.purchases/list-purchases-of-client client-data-updated all-purchases))

(defn -main
  [& args]
  (println "-------------------------------------------------")
  (println "Associated credit-card:" client-data-with-initial-credit-card-associated)
  (println "-------------------------------------------------")
  (println "Last transaction:" (last all-purchases))
  (println "-------------------------------------------------")
  (println "Credit Card most updated:" credit-card-most-updated)
  (println "-------------------------------------------------")
  (println "Client Data most updated:" client-data-updated)
  (println "-------------------------------------------------")
  (println "Purchases of the client:" purchases-client)
  (println "-------------------------------------------------")
  (println "Expenses by category:" (logic.purchases/group-purchases-by-category approved-purchases))
  (println "-------------------------------------------------")
  (println "Search by merchant using query: {:merchant iFood}:" (logic.purchases/search-purchases all-purchases {:purchase/merchant "iFood"}))
  (println "-------------------------------------------------")
  (println "Search by amount using query: {:amount 100}:" (logic.purchases/search-purchases all-purchases {:purchase/amount 100}))
  (println "---------------------------------------------------")
  (println "Monthly bill of month 05" (logic.purchases/monthly-bill approved-purchases 2021 5))
  (println "-------------------------------------------------")
  (println "Monthly bill of month 10" (logic.purchases/monthly-bill approved-purchases 2021 10)))

(-main)


