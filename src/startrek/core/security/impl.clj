(ns startrek.core.security.impl
  {:author "David Harrigan"}
  (:require
   [startrek.core.config.interface :as config]
   [startrek.core.domain.officer.interface :as officer]
   [startrek.core.errors.interface :as errors :refer [throw-unauthorized-exception]]
   [startrek.core.security.authentication-errors :as authentication-errors]
   [startrek.core.session.interface :as session]
   [startrek.core.utils.text :as text-utils])
  (:import
   [de.mkammerer.argon2 Argon2Factory Argon2Factory$Argon2Types Argon2id]
   [java.nio.charset StandardCharsets]))

(set! *warn-on-reflection* true)

(def ^:private argon2id (Argon2Factory/create Argon2Factory$Argon2Types/ARGON2id))

(defn ^:private valid-password?
  [password password-hash]
  (.verify ^Argon2id argon2id ^String password-hash (char-array password) StandardCharsets/UTF_8))

(defn ^:private save-session
  ([officer app-config] (save-session officer nil app-config))
  ([{:officer/keys [uuid] :as officer} previous-session-id app-config]
   (let [random-id (text-utils/random-id)
         identifiable-session-id (str random-id ":" uuid)
         officer' (dissoc officer :officer/password-hash)
         session {:session-id random-id :officer officer'}]
     (session/save-session {:session-id identifiable-session-id :session session} app-config)
     (when-not (config/keep-previous-session? app-config)
       (session/delete-session {:session-id previous-session-id} app-config))
     session)))

(defn ^:private find-officer
  [email-address app-config]
  (officer/find-officer {:email-address email-address} app-config))

(defn basic-authentication
  [{:keys [username password] :as credentials} app-config]
  (when-let [{:officer/keys [password-hash] :as officer} (find-officer username app-config)]
    (if (valid-password? password password-hash)
      (save-session officer app-config)
      (throw-unauthorized-exception authentication-errors/missing-or-invalid-basic-credentials))))

(defn cookie-authentication
  [{:keys [session-id] :as credentials} app-config]
  (if-let [{{:officer/keys [email-address]} :officer :as session} (and session-id (session/get-session {:session-id session-id} app-config))]
    (let [officer (find-officer email-address app-config)]
      (save-session officer session-id app-config))
    (throw-unauthorized-exception authentication-errors/missing-or-invalid-cookie-credentials)))

(defn token-authentication
  [{:keys [session-id] :as credentials} app-config]
  (if-let [{{:officer/keys [email-address]} :officer :as session} (and session-id (session/get-session {:session-id session-id} app-config))]
    (let [officer (find-officer email-address app-config)]
      (save-session officer session-id app-config))
    (throw-unauthorized-exception authentication-errors/missing-or-invalid-token-credentials)))
