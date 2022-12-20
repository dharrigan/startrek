(ns startrek.core.security.impl
  {:author "David Harrigan"}
  (:require
   [startrek.core.domain.officer.interface :as officer]
   [startrek.core.errors.interface :as errors :refer [throw-unauthorized-exception]]
   [startrek.core.security.authentication-errors :as authentication-errors])
  (:import
   [de.mkammerer.argon2 Argon2Factory Argon2Factory$Argon2Types Argon2id]
   [java.nio.charset StandardCharsets]))

(set! *warn-on-reflection* true)

(def ^:private argon2id (Argon2Factory/create Argon2Factory$Argon2Types/ARGON2id))

;;
;; An in-memory session, totally not suitable for any real productionised system..
;;
(def ^:private sessions (atom {}))

(defn ^:private find-officer
  [email-address app-config]
  (officer/find-officer {:email-address email-address} app-config))

(defn ^:private valid-password?
  [password password-hash]
  (.verify ^Argon2id argon2id ^String password-hash (char-array password) StandardCharsets/UTF_8))

;;
;; Not suitable for a productionised system. You may want to store the session
;; in Redis (or somewhere else) to facilitate load balancing. Also, not only
;; do you need to check if the password is valid, but perhaps the user would
;; fail some "business" logic checks, for exmaple, are they still a member or
;; whatnot...Thus, a set of post authentication checks needs to be done after
;; validating the user's password.
;;
;; These sessions keep on growing too...still, good enough as a starting point.
;;
(defn basic-authentication
  [{:keys [username password] :as credentials} app-config]
  (when-let [{:officer/keys [password-hash] :as officer} (find-officer username app-config)]
    (if (valid-password? password password-hash)
      (let [session-id (str (random-uuid))
            session {:session-id session-id :officer officer}]
        (reset! sessions (assoc @sessions session-id session))
        session)
      (throw-unauthorized-exception authentication-errors/missing-or-invalid-basic-credentials))))

;;
;; Another naive solution, but shows a basic starting position. Normally you
;; would want to perform some post "found from session" checks, like
;; double-checking the session-id matches, or perhaps their account has been
;; locked out (for some reason), so a set of post authentication checks needs to
;; be conducted after the user has been fetched from the session (cache).
;;
(defn token-authentication
  [{:keys [session-id] :as credentials}]
  (get @sessions session-id))
