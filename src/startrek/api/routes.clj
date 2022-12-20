(ns startrek.api.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.middleware.authentication :refer [private-authentication-middleware]]
   [startrek.api.officers.routes :as officers]
   [startrek.api.starships.routes :as starships]))

(set! *warn-on-reflection* true)

(defn private-routes
  []
  ["/private" {:middleware private-authentication-middleware}
   (starships/private-routes)])

(defn public-routes
  []
  ["/public"
   (officers/public-routes)
   (starships/public-routes)])

(defn routes
  []
  ["/api" (public-routes) (private-routes)])
