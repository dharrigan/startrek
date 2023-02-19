(ns startrek.ui.lcars.routes
  {:author "David Harrigan"}
  (:require
   [startrek.ui.lcars.authentication.routes :as authentication]
   [startrek.ui.lcars.middleware.authentication :refer [private-authentication-middleware]]
   [startrek.ui.lcars.starships.routes :as starships-routes]))

(set! *warn-on-reflection* true)

(defn ^:private private-routes
  []
  ["" {:middleware private-authentication-middleware}
   (authentication/private-routes)
   (starships-routes/private-routes)])

(defn ^:private public-routes
  []
  (authentication/public-routes))

(defn routes
  []
  ["/lcars" (public-routes) (private-routes)])
