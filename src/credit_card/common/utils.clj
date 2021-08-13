(ns credit-card.common.utils
  (:require [clojure.walk :as walk]))

(defn uuid
  []
  (java.util.UUID/randomUUID))

(defn add-uuid
  ([m]
   (if (get m :id)
     m
     (assoc m :id (uuid))))

  ([m namespace]
   (let [ns-keyword (keyword namespace "id")]
     (if (get m ns-keyword)
       m
       (assoc m ns-keyword (uuid))))))

(defn remove-db-id
  [m]
  (if (map? m)
    (dissoc m :db/id)
    m))

(defn remove-all-db-ids
  [map-or-coll]
  (walk/prewalk remove-db-id map-or-coll))
