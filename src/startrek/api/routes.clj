(ns startrek.api.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.starship.routes :as starship]))

(set! *warn-on-reflection* true)

(defn routes
  []
  ["/api" (starship/routes)])
