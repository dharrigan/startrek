(ns startrek.test.generators
  {:author "David Harrigan"}
  (:require
   [startrek.core.domain.starship.model :as starship]))

(defn starship
  [& overrides]
  (starship/gen-starship overrides))
