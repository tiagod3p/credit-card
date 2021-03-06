(ns credit-card.logic.purchases
  (:require [credit-card.logic.validations :as logic.validate]
            [credit-card.logic.credit-card :as logic.credit-card]
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
  (let [current-limit   (:credit-card/limit credit-card)
        expiration-date (:credit-card/expiration-date credit-card)
        valid-purchase  (logic.validate/valid-purchase?
                          (logic.validate/limit? current-limit amount)
                          (logic.validate/expired-card? expiration-date date))
        limit-to-update (if valid-purchase
                          (logic.credit-card/new-limit current-limit amount)
                          current-limit)
        credit-card-updated (update credit-card :credit-card/limit (constantly limit-to-update))]
    {:purchase/date        date
     :purchase/amount      amount
     :purchase/merchant    merchant
     :purchase/category    category
     :purchase/approved?   valid-purchase
     :purchase/credit-card credit-card-updated}))

(s/defn purchases-amount :- [models.general/NumGreaterOrEqualThanZero]
  [purchases             :- [models.purchase/Purchase]]
  (map :purchase/amount purchases))

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
    (group-by :purchase/category purchases)))

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
       (= month (.getMonthValue (:purchase/date purchase)))
       (= year (.getYear (:purchase/date purchase)))))
    purchases))

(s/defn monthly-bill :- models.general/NumGreaterOrEqualThanZero
  [purchases         :- [models.purchase/Purchase]
   year              :- models.general/NumGreaterOrEqualThanZero
   month             :- models.general/MonthValue]
  (let [monthly-purchases (search-purchases-by-year-and-month purchases year month)]
    (total-purchases-amount monthly-purchases)))

(s/defn list-purchases-of-client :- [models.purchase/Purchase]
  [client-data                   :- models.credit-card/ClientData
   purchases                     :- [models.purchase/Purchase]]
  (let [credit-cards-of-client (:client/credit-cards client-data)
        credit-cards-numbers-of-client (map #(get % :credit-card/number) credit-cards-of-client)]
    (filter (fn
              [purchase]
              (some #(= (get-in purchase [:purchase/credit-card :credit-card/number]) %) credit-cards-numbers-of-client))
            purchases)))
