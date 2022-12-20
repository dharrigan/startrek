(ns startrek.api.officers.handler
  {:author "David Harrigan"}
  (:require
   [startrek.api.officers.mapper :as mapper]
   [startrek.api.officers.transformations :as transformations]))

(set! *warn-on-reflection* true)

;;
;; you may want to do more post-login stuff here...
;;
(defn post-login-success
  [request]
  (-> request
      (mapper/request->post-login-success)
      (transformations/post-login-success->response)))
