(ns startrek.ui.lcars.authentication.routes
  {:author "David Harrigan"}
  (:require
   [ring.middleware.anti-forgery :as anti-forgery]
   [startrek.ui.lcars.authentication.handler :as handler]
   [startrek.ui.lcars.middleware.authentication :refer [public-authentication-middleware]]))

(set! *warn-on-reflection* true)

(defn private-routes
  [])

(defn public-routes
  []
  [["/login" {:middleware [anti-forgery/wrap-anti-forgery]}
    ["" {:get {:handler handler/login-page}
         :post {:middleware public-authentication-middleware
                :handler handler/post-login-success}}]] ;; it's post-login-success, as the middleware will have fetched (and checked) the user beforehand.
   ["/logout"
    ["" {:post {:handler handler/logout}}]]])
