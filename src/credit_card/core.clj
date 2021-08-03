(ns credit-card.core
  (:require [credit-card.logic.purchases :as logic.purchases]))

(def client-data {:full-name "Tiago Vidal"
                  :cpf       "999.999.999-99"
                  :email     "tiago@my-email.com.br"})

(def credit-card {:number          "111"
                  :cvv             "222"
                  :expiration-date (java.time.YearMonth/parse "2029-09")
                  :limit           1000})

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
                  (:credit-card purchase-1)))

(def purchase-3 (logic.purchases/purchase
                  (java.time.LocalDate/parse "2021-10-07")
                  100
                  "C&A"
                  "Clothing"
                  (:credit-card purchase-2)))

(def all-purchases [purchase-1 purchase-2 purchase-3])

(def approved-purchases (logic.purchases/search-purchases all-purchases {:approved? true}))

(defn -main
  [& args]
  (println "-------------------------------------------------")
  (println "Last transaction:" (last all-purchases))
  (println "-------------------------------------------------")
  (println "Credit Card most updated:" (:credit-card (last all-purchases)))
  (println "-------------------------------------------------")
  (println "Expenses by category:" (logic.purchases/group-purchases-by-category approved-purchases))
  (println "-------------------------------------------------")
  (println "Search by merchant using query: {:merchant iFood}:" (logic.purchases/search-purchases all-purchases {:merchant "iFood"}))
  (println "-------------------------------------------------")
  (println "Search by amount using query: {:amount 100}:" (logic.purchases/search-purchases all-purchases {:amount 100}))
  (println "---------------------------------------------------")
  (println "Monthly bill of month 05" (logic.purchases/monthly-bill approved-purchases 2021 5))
  (println "-------------------------------------------------")
  (println "Monthly bill of month 10" (logic.purchases/monthly-bill approved-purchases 2021 10)))

(-main)


