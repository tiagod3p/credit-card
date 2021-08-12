(defproject credit_card "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories {"my.datomic.com" {:url "https://my.datomic.com/repo"
                                   :creds :gpg}}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [prismatic/schema "1.1.12"]
                 [prismatic/schema-generators "0.1.3"]
                 [nubank/matcher-combinators "3.3.0"]
                 [com.datomic/datomic-pro "1.0.6269"]]
  :repl-options {:init-ns credit-card.core})
