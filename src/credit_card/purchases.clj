(ns credit-card.purchases
  (:require [credit-card.validations :as c.validations]
            [credit-card.models.credit-card :as models.credit-card]
            [credit-card.models.general :as models.general]
            [credit-card.models.purchase :as models.purchase]
            [schema.core :as s]))

(s/defn purchase :- models.purchase/Purchase
  [date          :- models.general/LocalDate
   amount        :- models.general/NumGreaterOrEqualThanZero
   merchant      :- s/Str
   category      :- s/Str
   credit-card   :- models.credit-card/CreditCard]
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

(s/defn purchases-amount :- [models.general/NumGreaterOrEqualThanZero]
  [purchases             :- [models.purchase/Purchase]]
  (map :amount purchases))

(s/defn total-purchases-amount  :- models.general/NumGreaterOrEqualThanZero
  [purchases                    :- [models.purchase/Purchase]]
  (reduce + (purchases-amount purchases)))

(s/defn total-purchases-amount-by-category :- models.purchase/CategoryAmount
  [[category-name purchases]]
  {category-name (total-purchases-amount purchases)})

(s/defn group-purchases-by-category :- [models.purchase/CategoryAmount]
  [purchases                        :- [models.purchase/Purchase]]
  (map
    total-purchases-amount-by-category
    (group-by :category purchases)))

(s/defn search-purchases :- [models.purchase/Purchase]
  [purchases             :- [models.purchase/Purchase]
   query                 :- models.general/Query]
  (filter
    (fn [purchase]
      (= query
         (select-keys purchase (keys query))))
    purchases))

(s/defn search-purchases-by-year-and-month :- [models.purchase/Purchase]
  [purchases                               :- [models.purchase/Purchase]
   year                                    :- models.general/NumGreaterOrEqualThanZero
   month                                   :- models.general/MonthValue]           
  (filter
    (fn [purchase]
      (and
        (= month (.getMonthValue (:date purchase)))
        (= year (.getYear (:date purchase)))))
    purchases))

(s/defn monthly-bill :- models.general/NumGreaterOrEqualThanZero
  [purchases         :- [models.purchase/Purchase]    
   year              :- models.general/NumGreaterOrEqualThanZero
   month             :- models.general/MonthValue]
  (let [monthly-purchases (search-purchases-by-year-and-month purchases year month)]
    (total-purchases-amount monthly-purchases)))

