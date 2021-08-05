(ns credit-card.logic.purchases-test
  (:require [clojure.test :refer :all]
            [credit-card.logic.purchases :as logic.purchases]
            [credit-card.models.credit-card :as models.credit-card]
            [schema.core :as s]
            [matcher-combinators.test :refer [match?]]))

(deftest purchase-test
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          purchase-date (java.time.LocalDate/parse "2029-08-01")
          amount        1000
          merchant      "Burger King"
          category      "Food"
          purchase      (logic.purchases/purchase purchase-date amount merchant category credit-card)]
      (testing "should validate this purchase attempt because its valid"
        (is (match? {:approved? true} purchase))
        (is (match? {:credit-card {:limit 0}} purchase))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          purchase-date (java.time.LocalDate/parse "2029-08-01")
          amount        1001
          merchant      "Burger King"
          category      "Food"
          purchase      (logic.purchases/purchase purchase-date amount merchant category credit-card)]
      (testing "should reprove this purchase attempt because its limit is invalid"
        (is (match? {:approved? false} purchase))
        (is (match? {:credit-card {:limit 1000}} purchase))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          purchase-date (java.time.LocalDate/parse "2029-09-01")
          amount        1000
          merchant      "Burger King"
          category      "Food"
          purchase      (logic.purchases/purchase purchase-date amount merchant category credit-card)]
      (testing "should reprove this purchase attempt because its card is expired"
        (is (match? {:approved? false} purchase))
        (is (match? {:credit-card {:limit 1000}} purchase))))))

(deftest list-purchases-of-client
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          client-data {:full-name "Tiago Vidal"
                       :cpf       "999.999.999-99"
                       :email     "tiago@my-email.com.br"
                       :credit-cards [{:number          "111"
                                       :cvv             "222"
                                       :expiration-date (java.time.YearMonth/parse "2029-09")
                                       :limit           1000},
                                      {:number          "001"
                                       :cvv             "222"
                                       :expiration-date (java.time.YearMonth/parse "2029-09")
                                       :limit           1000}]}

          purchase-date (java.time.LocalDate/parse "2029-08-01")
          amount        1000
          merchant      "Burger King"
          category      "Food"
          purchase      (logic.purchases/purchase purchase-date amount merchant category credit-card)
          all-purchases [purchase]
          purchases-of-the-client (logic.purchases/list-purchases-of-client client-data all-purchases)]
      (testing "client should have 1/1 purchase counted"
        (is (= 1 (count purchases-of-the-client)))
        (is (= 1 (count all-purchases)))))

    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          client-data {:full-name "Tiago Vidal"
                       :cpf       "999.999.999-99"
                       :email     "tiago@my-email.com.br"
                       :credit-cards [{:number          "111"
                                       :cvv             "222"
                                       :expiration-date (java.time.YearMonth/parse "2029-09")
                                       :limit           1000},
                                      {:number          "001"
                                       :cvv             "222"
                                       :expiration-date (java.time.YearMonth/parse "2029-09")
                                       :limit           1000}]}

          credit-card-of-other-client {:number          "999"
                                       :cvv             "190"
                                       :expiration-date (java.time.YearMonth/parse "2029-09")
                                       :limit           500}

          purchase-date (java.time.LocalDate/parse "2029-08-01")
          amount        1000
          merchant      "Burger King"
          category      "Food"
          purchase      (logic.purchases/purchase purchase-date amount merchant category credit-card-of-other-client)
          all-purchases [purchase]
          purchases-of-the-client (logic.purchases/list-purchases-of-client client-data all-purchases)]
      (testing "client should have 0/1 purchase counted"
        (is (= 0 (count purchases-of-the-client)))
        (is (= 1 (count all-purchases)))))

    (let [first-credit-card-of-client  {:number          "111"
                                        :cvv             "222"
                                        :expiration-date (java.time.YearMonth/parse "2029-09")
                                        :limit           1000}

          second-credit-card-of-client {:number          "001"
                                        :cvv             "222"
                                        :expiration-date (java.time.YearMonth/parse "2029-09")
                                        :limit           1000}

          client-data                  {:full-name "Tiago Vidal"
                                        :cpf       "999.999.999-99"
                                        :email     "tiago@my-email.com.br"
                                        :credit-cards [first-credit-card-of-client,
                                                      second-credit-card-of-client]}

          credit-card-of-other-client  {:number          "999"
                                        :cvv             "190"
                                        :expiration-date (java.time.YearMonth/parse "2029-09")
                                        :limit           500}

          purchase-date                (java.time.LocalDate/parse "2029-08-01")
          amount                       1000
          merchant                     "Burger King"
          category                     "Food"
          first-purchase-of-client     (logic.purchases/purchase purchase-date amount merchant category first-credit-card-of-client)
          second-purchase-of-client    (logic.purchases/purchase purchase-date amount merchant category second-credit-card-of-client)
          purchase-of-other-client     (logic.purchases/purchase purchase-date amount merchant category credit-card-of-other-client)
          all-purchases                [first-purchase-of-client second-purchase-of-client purchase-of-other-client]
          purchases-of-the-client      (logic.purchases/list-purchases-of-client client-data all-purchases)]
      (testing "client should have 2/3 purchase counted realized with 2 different cards"
        (is (= 2 (count purchases-of-the-client)))
        (is (= 3 (count all-purchases)))))))

(deftest group-purchases-by-category
  (s/with-fn-validation
    (let [credit-card             {:number          "111"
                                   :cvv             "222"
                                   :expiration-date (java.time.YearMonth/parse "2029-09")
                                   :limit           1000}

          client-data             {:full-name "Tiago Vidal"
                                   :cpf       "999.999.999-99"
                                   :email     "tiago@my-email.com.br"
                                   :credit-cards [{:number          "111"
                                                   :cvv             "222"
                                                   :expiration-date (java.time.YearMonth/parse "2029-09")
                                                   :limit           1000},
                                                  {:number          "001"
                                                   :cvv             "222"
                                                   :expiration-date (java.time.YearMonth/parse "2029-09")
                                                   :limit           1000}]}

          purchase-date           (java.time.LocalDate/parse "2029-08-01")
          amount                  500
          merchant                "Burger King"
          category                "Food"
          first-purchase          (logic.purchases/purchase purchase-date amount merchant category credit-card)
          second-purchase         (logic.purchases/purchase purchase-date amount merchant category credit-card)
          all-purchases           [first-purchase second-purchase]
          purchases-of-the-client (logic.purchases/list-purchases-of-client client-data all-purchases)
          total-by-category       (logic.purchases/group-purchases-by-category purchases-of-the-client)]
      (testing "should return price equals to 1000 in the category Food"
        (is (match? [{"Food" 1000}] total-by-category))))))

