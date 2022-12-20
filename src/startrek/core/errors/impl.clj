(ns startrek.core.errors.impl
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private internal-server-error 500)
(def ^:private unauthorized 401)
(def ^:private not-found 404)
(def ^:private conflict 409)

(defn throw-database-exception
  [{:keys [message data cause] :as error}]
  (throw (ex-info message (assoc data :http-status internal-server-error) cause)))

(defn throw-resource-does-not-exist-exception
  [{:keys [message data cause] :as error}]
  (throw (ex-info message (assoc data :http-status not-found) cause)))

(defn throw-resource-exists-exception
  [{:keys [message data cause] :as error}]
  (throw (ex-info message (assoc data :http-status conflict) cause)))

(defn throw-unauthorized-exception
  [{:keys [message data cause] :as error}]
  (throw (ex-info message (assoc data :http-status unauthorized) cause)))
