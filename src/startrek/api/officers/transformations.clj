(ns startrek.api.officers.transformations
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]))

(set! *warn-on-reflection* true)

(defn post-login-success->response
  [{{:keys [session-id]} :identity :as request}]
  (response/response #:login{:session-id session-id}))

(defn logout->response
  []
  (response/response nil))
