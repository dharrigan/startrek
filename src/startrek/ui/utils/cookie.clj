(ns startrek.ui.utils.cookie
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]
   [startrek.core.config.interface :as config]))

(set! *warn-on-reflection* true)

(def ^:private thirty-days-in-seconds (* 60 60 24 30))

(defn cookie-defaults
  ([app-config] (cookie-defaults thirty-days-in-seconds app-config))
  ([max-age app-config]
   (let [{:keys [secure?]} (config/cookies app-config)]
     (cond-> {:http-only true :path "/" :same-site :lax}
       max-age (assoc :max-age max-age)
       secure? (assoc :secure true :same-site :strict)))))

(defn get-lcars-session-cookie
  [{:keys [app-config] :as request}]
  (let [{:keys [session-cookie-name]} (config/cookies app-config)]
    (get-in request [:cookies session-cookie-name :value])))

(defn get-lcars-token-cookie
  [{:keys [app-config] :as request}]
  (let [{:keys [token-cookie-name]} (config/cookies app-config)]
    (get-in request [:cookies token-cookie-name :value])))

(defn set-lcars-session-cookie
  [response session-id app-config]
  (let [{:keys [session-cookie-name]} (config/cookies app-config)]
    (response/set-cookie response session-cookie-name session-id (cookie-defaults app-config))))

(defn unset-lcars-session-cookie
  [response app-config]
  (let [{:keys [session-cookie-name]} (config/cookies app-config)]
    (response/set-cookie response session-cookie-name nil (cookie-defaults -1 app-config))))

(defn unset-lcars-token-cookie
  [response app-config]
  (let [{:keys [token-cookie-name]} (config/cookies app-config)]
    (response/set-cookie response token-cookie-name nil (cookie-defaults -1 app-config))))
