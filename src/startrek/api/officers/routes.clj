(ns startrek.api.officers.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.middleware.authentication :refer [public-authentication-middleware]]
   [startrek.api.officers.handler :as handler]
   [startrek.api.officers.spec :as spec]
   [startrek.shared.constants :refer [application-json ok]]))

(set! *warn-on-reflection* true)

(def ^:private officers-login-api-version "application/vnd.startrek.officers.login.v1+json;charset=utf-8")

(defn public-routes
  []
  ["/officers"
   ["/login" {:middleware public-authentication-middleware
              :post {:handler handler/post-login-success ;; it's post-login-success, as the middleware will have fetched (and checked) the officer beforehand.
                     :responses {ok {:body spec/post-login-response}}
                     :swagger {:consumes [application-json]
                               :produces [officers-login-api-version]}}}]])
