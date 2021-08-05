(ns credit-card.logic.validations-test
  (:require [clojure.test :refer :all]
            [credit-card.logic.validations :as logic.validate]
            [schema.core :as s]))

(deftest expired-card?
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          expiration-date (:expiration-date credit-card)

          purchase-date (java.time.LocalDate/parse "2029-08-30")]
      (testing "should return false (card not expired) if the purchase date is at least 1 month before the expiration date"
        (is (= false
               (logic.validate/expired-card?
                expiration-date
                purchase-date)))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          expiration-date (:expiration-date credit-card)

          purchase-date (java.time.LocalDate/parse "2029-09-01")]
      (testing "should return true (card expired) if the purchase date is in any day of the month of the expiration date"
        (is (= true
               (logic.validate/expired-card?
                expiration-date
                purchase-date)))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          expiration-date (:expiration-date credit-card)

          purchase-date (java.time.LocalDate/parse "2029-10-01")]
      (testing "should return true (card expired) if the purchase date is in any day after the month of the expiration date"
        (is (= true
               (logic.validate/expired-card?
                expiration-date
                purchase-date)))))))


(deftest limit?
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 999]
      (testing "should return true (valid limit) if the purchase amount is less than limit"
        (is (= true
               (logic.validate/limit?
                limit
                purchase-amount)))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 1000]
      (testing "should return true (valid limit) if the purchase amount is equal than limit"
        (is (= true
               (logic.validate/limit?
                limit
                purchase-amount)))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 1001]
      (testing "should return false (invalid limit) if the purchase amount is greater than limit"
        (is (= false
               (logic.validate/limit?
                limit
                purchase-amount)))))))


(deftest valid-purchase?
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 999

          purchase-date (java.time.LocalDate/parse "2029-08-30")

          expiration-date (:expiration-date credit-card)]
      (testing "should return true (valid purchase) if the card is not expired and the limit is valid"
        (is (= true
               (logic.validate/valid-purchase?
                (logic.validate/limit? limit purchase-amount)
                (logic.validate/expired-card? expiration-date purchase-date))))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 1001

          purchase-date (java.time.LocalDate/parse "2029-08-30")

          expiration-date (:expiration-date credit-card)]
      (testing "should return false (invalid purchase) if the card is not expired and the limit is invalid"
        (is (= false
               (logic.validate/valid-purchase?
                (logic.validate/limit? limit purchase-amount)
                (logic.validate/expired-card? expiration-date purchase-date))))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 999

          purchase-date (java.time.LocalDate/parse "2029-09-30")

          expiration-date (:expiration-date credit-card)]
      (testing "should return false (invalid purchase) if the card is expired and the limit is valid"
        (is (= false
               (logic.validate/valid-purchase?
                (logic.validate/limit? limit purchase-amount)
                (logic.validate/expired-card? expiration-date purchase-date))))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          limit (:limit credit-card)

          purchase-amount 1001

          purchase-date (java.time.LocalDate/parse "2029-09-30")

          expiration-date (:expiration-date credit-card)]
      (testing "should return false (invalid purchase) if the card is expired and the limit is invalid"
        (is (= false
               (logic.validate/valid-purchase?
                (logic.validate/limit? limit purchase-amount)
                (logic.validate/expired-card? expiration-date purchase-date))))))))

