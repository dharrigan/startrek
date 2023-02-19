(ns startrek.api.officers.handler
  {:author "David Harrigan"}
  (:require
   [startrek.api.officers.mapper :as mapper]
   [startrek.api.officers.transformations :as transformations]
   [startrek.core.session.interface :as session]
   [startrek.shared.constants :refer [authorization]]))

(set! *warn-on-reflection* true)

(defn ^:private parse-authorization-header
  [request]
  (some->> (get-in request [:headers authorization])
           (re-find (re-pattern "(?i)^token (.+)$"))
           (second)))

;;
;; you may want to do more post-login stuff here...
;;
(defn post-login-success
  [request]
  (-> request
      (mapper/request->post-login-success)
      (transformations/post-login-success->response)))

(defn logout
  [{:keys [app-config] :as request}]
  (when-let [session-id (parse-authorization-header request)]
    (session/delete-session {:session-id session-id} app-config))
  (transformations/logout->response))
