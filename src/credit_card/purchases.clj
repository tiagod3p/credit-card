(ns credit-card.purchases
  (:require [credit-card.validations :as c.validations]))

(defn purchase
  [date amount merchant category credit-card]
  (let [current-limit   (:limit credit-card)
        expiration-date (:expiration-date credit-card)
        valid-purchase  (c.validations/valid-purchase?
                          (c.validations/limit? current-limit amount)
                          (c.validations/expired-card? expiration-date date))
        limit-to-update (if valid-purchase
                          (c.validations/new-limit current-limit amount)
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
