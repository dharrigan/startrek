(ns startrek.shared.middleware.sessions
  {:author "David Harrigan"}
  (:require
   [jsonista.core :as json]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.session.store :refer [SessionStore]]
   [startrek.core.cache.interface :as cache]
   [startrek.core.config.interface :as config]
   [startrek.core.utils.text :as text-utils]
   [startrek.ui.utils.cookie :refer [cookie-defaults]]))

(set! *warn-on-reflection* true)

(def ^:private keyword-mapper (json/object-mapper {:strip-nils true :decode-key-fn true}))
(def ^:private max-age nil)
(def ^:private lcars-token "lcars:token:")
(def ^:private one-hour-in-ms (* 1000 60 60)) ;; 3600000
(def ^:private token-expiry {:expiry one-hour-in-ms})

(defn ^:private delete-token
  [token-id app-config]
  (cache/redis-del (str lcars-token token-id) app-config))

(defn ^:private get-token
  [token-id app-config]
  (-> (cache/redis-get (str lcars-token token-id) app-config)
      (json/read-value keyword-mapper)))

(defn ^:private save-token
  [token-id token app-config]
  (cache/redis-put (str lcars-token token-id) token token-expiry app-config))

;; I wonder, am I getting a new RedisStore upon each route request?? (see wrap-session and :store below)
(deftype ^:private RedisStore [app-config]
  SessionStore
  (read-session [_ key]
    (and key (get-token key app-config)))
  (write-session [_ key token]
    (let [token-id (or key (text-utils/random-id))]
      (save-token token-id token app-config)
      token-id))
  (delete-session [_ key]
    (delete-token key app-config)))

(defn ^:private with-session
  [handler app-config]
  (let [{:keys [token-cookie-name]} (config/cookies app-config)]
    (wrap-session handler {:store (RedisStore. app-config)
                           :cookie-name token-cookie-name ;; a simple store, gets reset on application restart (not really suitable for a load-balanced solution...)
                           :cookie-attrs (cookie-defaults max-age app-config)})))

(def sessions-middleware
  {:name ::cors
   :description "Adds session functionality to each request/response."
   :wrap with-session})
