(ns startrek.ui.lcars.middleware.authentication
  {:author "David Harrigan"}
  (:require
   [buddy.auth :refer [authenticated?]]
   [buddy.auth.backends.httpbasic :refer [http-basic-backend]]
   [buddy.auth.middleware :refer [wrap-authentication]]
   [buddy.auth.protocols :as proto]
   [clojure.tools.logging :as log]
   [startrek.core.config.interface :as config]
   [startrek.core.errors.interface :refer [throw-unauthorized-exception]]
   [startrek.core.security.authentication-errors :as authentication-errors]
   [startrek.core.security.interface :as security]
   [startrek.ui.utils.cookie :as cookie-utils]))

(set! *warn-on-reflection* true)

(defn ^:private basic-authentication
  [{:keys [app-config] :as request} {:keys [username] :as credentials}]
  (log/debugf "Performing BASIC authentication for officer '%s'." username)
  (security/basic-authentication credentials app-config))

(defn ^:private wrap-basic-authentication
  [handler]
  (let [authenticator (http-basic-backend {:realm "startrek" :authfn basic-authentication})]
    (wrap-authentication handler authenticator)))

(defn ^:private wrap-post-basic-authentication
  [handler]
  (fn [{:keys [identity] :as request}]
    (let [{{:officer/keys [email-address]} :officer} identity]
      (log/debugf "Performing POST BASIC authentication for officer '%s'." email-address)
      (if (authenticated? request)
        (do
          (log/debugf "POST BASIC authentication completed for officer '%s'." email-address)
          (assoc (handler request) :identity identity)) ;; associate the identity to the response so we can use it on the way out...
        (throw-unauthorized-exception authentication-errors/missing-or-invalid-basic-credentials)))))

(defn ^:private cookie-authentication
  [{:keys [app-config] :as request} {:keys [session-id] :as credentials}]
  (log/debugf "Performing COOKIE authentication for lcars-session '%s'." session-id)
  (security/cookie-authentication credentials app-config))

(defn ^:private handle-unauthorized-default
  [request]
  (if (authenticated? request)
    {:status 403 :headers {} :body "Permission denied"}
    {:status 401 :headers {} :body "Unauthorized"}))

(defn ^:private cookie-backend
  [{:keys [authfn unauthorized-handler]}]
  {:pre [(ifn? authfn)]}
  (reify
    proto/IAuthentication
    (-parse [_ {:keys [app-config] :as request}]
      (let [{:keys [session-cookie-name]} (config/cookies app-config)]
        {:session-id (get-in request [:cookies session-cookie-name :value])}))
    (-authenticate [_ request credentials]
      (authfn request credentials))
    proto/IAuthorization
    (-handle-unauthorized [_ request metadata]
      (if unauthorized-handler
        (unauthorized-handler request metadata)
        (handle-unauthorized-default request)))))

(defn ^:private wrap-cookie-authentication
  [handler]
  (let [authenticator (cookie-backend {:authfn cookie-authentication})]
    (wrap-authentication handler authenticator)))

(defn ^:private wrap-post-cookie-authentication
  [handler]
  (fn [{:keys [identity app-config] :as request}]
    (if-let [{{:officer/keys [email-address]} :officer} identity]
      (do
        (log/debugf "Performing POST COOKIE authentication for officer '%s'." email-address)
        (if (authenticated? request)
          (let [{:keys [session-id]} identity]
            (log/debugf "POST COOKIE authentication completed for officer '%s'." email-address)
            (-> (handler request)
                (assoc :identity identity) ;; associate the identity to the response so we can use it on the way out...
                (cookie-utils/set-lcars-session-cookie session-id app-config))) ;; send up the new session, replacing the old one.
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

(def ^:private cookie-authentication-middleware
  {:name ::cookie-authentication
   :description "Wraps the handler in order to apply cookie 'lcars-token' authentication to a protected resource."
   :wrap wrap-cookie-authentication})

(def ^:private post-cookie-authentication-middleware
  {:name ::post-token-authentication
   :description "Checks the request to determine if the cookie 'lcars-token' authentication has succeeded."
   :wrap wrap-post-cookie-authentication})

(def public-authentication-middleware [basic-authentication-middleware post-basic-authentication-middleware])
(def private-authentication-middleware [cookie-authentication-middleware post-cookie-authentication-middleware])
