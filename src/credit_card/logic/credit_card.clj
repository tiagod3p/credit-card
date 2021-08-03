(ns credit-card.logic.credit-card
  (:require [schema.core :as s]
            [credit-card.models.general :as models.general]))

(s/defn new-limit :- models.general/NumGreaterOrEqualThanZero
  [limit        :- models.general/NumGreaterOrEqualThanZero
   amount       :- models.general/NumGreaterOrEqualThanZero]
  (- limit amount))
