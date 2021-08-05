(ns credit-card.logic.credit-card-test
  (:require [clojure.test :refer :all]
            [credit-card.logic.credit-card :as logic.credit-card]
            [schema.core :as s]))

(deftest associate-credit-card-with-client
  (s/with-fn-validation
    (let [credit-card {:number          "111"
                       :cvv             "222"
                       :expiration-date (java.time.YearMonth/parse "2029-09")
                       :limit           1000}

          client-data-without-credit-card {:full-name "Tiago Vidal"
                                           :cpf       "999.999.999-99"
                                           :email     "tiago@my-email.com.br"
                                           :credit-cards []}

          client-data-with-credit-card {:full-name "Tiago Vidal"
                                        :cpf       "999.999.999-99"
                                        :email     "tiago@my-email.com.br"
                                        :credit-cards [{:number          "111"
                                                      :cvv             "222"
                                                      :expiration-date (java.time.YearMonth/parse "2029-09")
                                                      :limit           1000}]}]
      (testing "should associate credit card with client if there is no credit card in client-data"
        (is (= client-data-with-credit-card
               (logic.credit-card/associate-credit-card-with-client
                credit-card
                client-data-without-credit-card)))))

    (let [new-credit-card {:number          "111"
                           :cvv             "222"
                           :expiration-date (java.time.YearMonth/parse "2029-09")
                           :limit           1000}
          client-data-with-two-old-credit-cards {:full-name "Tiago Vidal"
                                                 :cpf       "999.999.999-99"
                                                 :email     "tiago@my-email.com.br"
                                                 :credit-cards [{:number          "100"
                                                                 :cvv             "222"
                                                                 :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                 :limit           1000},
                                                                {:number          "001"
                                                                 :cvv             "222"
                                                                 :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                :limit           1000}]}
          client-data-with-new-credit-card {:full-name "Tiago Vidal"
                                            :cpf       "999.999.999-99"
                                            :email     "tiago@my-email.com.br"
                                            :credit-cards [{:number          "100"
                                                            :cvv             "222"
                                                            :expiration-date (java.time.YearMonth/parse "2029-09")
                                                            :limit           1000},
                                                           {:number          "001"
                                                            :cvv             "222"
                                                            :expiration-date (java.time.YearMonth/parse "2029-09")
                                                            :limit           1000},
                                                           {:number          "111"
                                                            :cvv             "222"
                                                            :expiration-date (java.time.YearMonth/parse "2029-09")
                                                            :limit           1000}]}]
      (testing "should associate a new credit card with client if there is already different credit cards in client-data"
        (is (= client-data-with-new-credit-card
               (logic.credit-card/associate-credit-card-with-client
                new-credit-card client-data-with-two-old-credit-cards)))))

    (let [new-credit-card {:number          "111"
                           :cvv             "222"
                           :expiration-date (java.time.YearMonth/parse "2029-09")
                           :limit           1000}
          client-data-with-two-old-credit-cards {:full-name "Tiago Vidal"
                                                 :cpf       "999.999.999-99"
                                                 :email     "tiago@my-email.com.br"
                                                 :credit-cards [{:number          "111"
                                                                 :cvv             "222"
                                                                 :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                 :limit           2000},
                                                                {:number          "001"
                                                                 :cvv             "222"
                                                                 :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                :limit           1000}]}
          client-data-with-credit-card-updated {:full-name "Tiago Vidal"
                                                :cpf       "999.999.999-99"
                                                :email     "tiago@my-email.com.br"
                                                :credit-cards [{:number          "111"
                                                                :cvv             "222"
                                                                :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                :limit           1000},
                                                               {:number          "001"
                                                                :cvv             "222"
                                                                :expiration-date (java.time.YearMonth/parse "2029-09")
                                                                :limit           1000}]}]
      (testing "should update associated credit card with client if there is already the same credit card in client-data"
        (is (= client-data-with-credit-card-updated
               (logic.credit-card/associate-credit-card-with-client
                new-credit-card client-data-with-two-old-credit-cards)))))))
