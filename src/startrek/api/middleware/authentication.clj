(ns startrek.api.middleware.authentication
  {:author "David Harrigan"}
  (:require
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
   [buddy.auth.backends.token :refer [token-backend]]
   [buddy.auth.middleware :refer [wrap-authentication]]
   [clojure.tools.logging :as log]
   [startrek.core.errors.interface :as errors :refer [throw-unauthorized-exception]]
   [startrek.core.security.authentication-errors :as authentication-errors]
   [startrek.core.security.interface :as security]))

(set! *warn-on-reflection* true)

(defn ^:private basic-authentication
  [{:keys [app-config] :as request} {:keys [username] :as credentials}]
  (log/debugf "Performing BASIC authentication for officer '%s'." (or username "UNKNOWN"))
  (security/basic-authentication credentials app-config))

(defn ^:private wrap-basic-authentication
  [handler]
  (let [authenticator (http-basic-backend {:realm "startrek" :authfn basic-authentication})]
    (wrap-authentication handler authenticator)))

(defn ^:private wrap-post-basic-authentication
  [handler]
  (fn [{:keys [identity] :as request}]
    (if-let [{{:officer/keys [email-address]} :officer} identity]
      (do
       (log/debugf "Performing POST BASIC authentication for officer '%s'." email-address)
       (if (authenticated? request)
         (do
          (log/debugf "POST BASIC authentication completed for officer '%s'." email-address)
          (handler request))
         (throw-unauthorized-exception authentication-errors/missing-or-invalid-basic-credentials)))
      (throw-unauthorized-exception authentication-errors/missing-or-invalid-basic-credentials))))

(defn ^:private token-authentication
  [request session-id]
  (log/debugf "Performing TOKEN authentication for session '%s'." session-id)
  (security/token-authentication {:session-id session-id}))

(defn ^:private wrap-token-authentication
  [handler]
  (let [authenticator (token-backend {:authfn token-authentication})]
    (wrap-authentication handler authenticator)))

(defn ^:private wrap-post-token-authentication
  [handler]
  (fn [{:keys [identity] :as request}]
    (if-let [{{:officer/keys [email-address]} :officer} identity]
      (do
       (log/debugf "Performing POST TOKEN authentication for officer '%s'." email-address)
       (if (authenticated? request)
         (do
          (log/debugf "POST TOKEN authentication completed for officer '%s'." email-address)
          (-> (handler request)
              (assoc :identity identity))) ;; associate the identity to the response so we can use it on the way out...
         (throw-unauthorized-exception authentication-errors/missing-or-invalid-token-credentials)))
      (throw-unauthorized-exception authentication-errors/missing-or-invalid-token-credentials))))

(def ^:private basic-authentication-middleware
  {:name ::basic-authentication
   :description "Wraps the handler in order to apply basic authentication to a protected resource."
   :wrap wrap-basic-authentication})

(def ^:private post-basic-authentication-middleware
  {:name ::post-basic-authentication
   :description "Checks the request to determine if basic authentication has succeeded."
   :wrap wrap-post-basic-authentication})

(def ^:private token-authentication-middleware
  {:name ::token-authentication
   :description "Wraps the handler in order to apply token authentication to a protected resource."
   :wrap wrap-token-authentication})

(def ^:private post-token-authentication-middleware
  {:name ::post-token-authentication
   :description "Checks the request to determine if token authentication has succeeded."
   :wrap wrap-post-token-authentication})

(def public-authentication-middleware [basic-authentication-middleware post-basic-authentication-middleware])
(def private-authentication-middleware [token-authentication-middleware post-token-authentication-middleware])
