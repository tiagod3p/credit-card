(ns credit-card.core)

(def client-data {:full-name "Tiago Vidal"
                  :cpf       "999.999.999-99"
                  :email     "tiago@my-email.com.br"})

(def credit-card {:number          "111"
                  :cvv             "222"
                  :expiration-date (java.time.YearMonth/parse "2029-09")
                  :limit           1000})

(defn expired-card?
  [expiration-date purchase-date]
  (let [purchase-date-year-and-month (java.time.YearMonth/from purchase-date)
        chrono-months (java.time.temporal.ChronoUnit/MONTHS)
        diff-in-months (.until purchase-date-year-and-month expiration-date chrono-months)]
    (<= diff-in-months 0)))

(defn limit?
  [limit amount]
  (>= limit amount))

(defn valid-purchase?
  [limit? expired-card?]
  (and limit? (not expired-card?)))

(defn new-limit
  [limit amount]
  (- limit amount))

(defn purchase
  [date amount merchant category credit-card]
  (let [current-limit (:limit credit-card)
        expiration-date (:expiration-date credit-card)
        valid-purchase (valid-purchase?
                         (limit? current-limit amount)
                         (expired-card? expiration-date date))
        limit-to-update (if valid-purchase
                          (new-limit current-limit amount)
                          current-limit)
        credit-card-updated (update credit-card :limit (constantly limit-to-update))]
    {:date        date
     :amount      amount
     :merchant    merchant
     :category    category
     :approved?   valid-purchase
     :credit-card credit-card-updated}))

(defn purchases-amount
  [purchases]
  (map :amount purchases))

(defn total-purchases-amount
  [purchases]
  (reduce + (purchases-amount purchases)))

(defn total-purchases-amount-by-category
  [[category-name purchases]]
  {category-name (total-purchases-amount purchases)})

(defn group-purchases-by-category
  [purchases]
  (map
    total-purchases-amount-by-category
    (group-by :category purchases)))

(defn search-purchases
  [purchases query]
  (filter
    (fn [purchase]
      (= query
         (select-keys purchase (keys query))))
    purchases))

(defn search-purchases-by-year-and-month
  [purchases year month]
  (filter
    (fn [purchase]
      (and
        (= month (.getMonthValue (:date purchase)))
        (= year (.getYear (:date purchase)))))
    purchases))

(defn monthly-bill
  [purchases year month]
  (let [monthly-purchases (search-purchases-by-year-and-month purchases year month)]
    (total-purchases-amount monthly-purchases)))

(def purchase-1 (purchase (java.time.LocalDate/parse "2021-05-07")
                          199
                          "iFood"
                          "Restaurant"
                          credit-card))

(def purchase-2 (purchase (java.time.LocalDate/parse "2021-10-07")
                          95
                          "C&A"
                          "Clothing"
                          (:credit-card purchase-1)))

(def purchase-3 (purchase (java.time.LocalDate/parse "2021-10-07")
                          100
                          "C&A"
                          "Clothing"
                          (:credit-card purchase-2)))

(def all-purchases [purchase-1 purchase-2 purchase-3])

(def approved-purchases (search-purchases all-purchases {:approved? true}))

(defn -main
  [& args]
  (println "-------------------------------------------------")
  (println "Last transaction:" (last all-purchases))
  (println "-------------------------------------------------")
  (println "Credit Card most updated:" (:credit-card (last all-purchases)))
  (println "-------------------------------------------------")
  (println "Expenses by category:" (group-purchases-by-category approved-purchases))
  (println "-------------------------------------------------")
  (println "Search by merchant using query: {:merchant iFood}:" (search-purchases all-purchases {:merchant "iFood"}))
  (println "-------------------------------------------------")
  (println "Search by amount using query: {:amount 100}:" (search-purchases all-purchases {:amount 100}))
  (println "---------------------------------------------------")
  (println "Monthly bill of month 05" (monthly-bill approved-purchases 2021 5))
  (println "-------------------------------------------------")
  (println "Monthly bill of month 10" (monthly-bill approved-purchases 2021 10)))

(-main)
