(ns startrek.system
  {:author "David Harrigan"}
  (:require
   [aero.core :refer [read-config]]
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [donut.system :as ds]
   [startrek.core.aux.thymeleaf.impl :as thymeleaf]
   [startrek.core.cache.impl :as cache]
   [startrek.core.config.interface :as config]
   [startrek.core.database.impl :as db]
   [startrek.core.jmxmp.impl :as jmxmp]
   [startrek.router :as router]))

(set! *warn-on-reflection* true)

(defn ^:private validate-config
  [config-file config]
  (log/infof "Validating '%s' to make sure it's good!" config-file)
  (if-let [errors (config/validate config)]
    (let [message (format "Bad '%s'! Errors are '%s'." config-file errors)]
      (throw (ex-info message {:message message :data {:message message :config-file config-file :errors errors}})))
    config))

(defn ^:private load-config
  [environment]
  (let [config-file (str "config/config" (when-not (= :production environment) (str "-" (name environment))) ".edn")]
    (log/infof "Loading config file '%s'." config-file)
    (->> (io/resource config-file)
         (read-config)
         (config/apply-defaults)
         (validate-config config-file))))

(def ^:private base-system
  {::ds/defs
   {:env {}
    :app-config {:db #::ds{:start (fn [{:keys [::ds/config]}] (db/start config))
                           :post-start (fn [{:keys [::ds/instance ::ds/config]}] (db/post-start instance config))
                           :stop (fn [{:keys [::ds/instance]}] (db/stop instance))
                           :config {:db (ds/ref [:env :secrets :db])
                                    :runtime-config (ds/ref [:env :runtime-config])}}

                 :sessions-cache #::ds{:start (fn [{:keys [::ds/config]}] (cache/start config))
                                       :stop (fn [{:keys [::ds/instance]}] (cache/stop instance))
                                       :config (ds/ref [:env :secrets :redis :sessions])}

                 :router #::ds{:start (fn [{:keys [::ds/config]}] (router/start config))
                               :stop (fn [{:keys [::ds/instance]}] (router/stop instance))
                               :config {:db (ds/ref [:app-config :db])
                                        :runtime-config (ds/ref [:env :runtime-config])
                                        :sessions-cache (ds/ref [:app-config :sessions-cache])
                                        :template-engine (ds/ref [:app-config :thymeleaf])}}

                 :jmxmp #::ds{:start (fn [{:keys [::ds/config]}] (jmxmp/start config))
                              :stop (fn [{:keys [::ds/instance]}] (jmxmp/stop instance))
                              :config (ds/ref [:env :runtime-config :jmxmp])}

                 :thymeleaf #::ds{:start (fn [{:keys [::ds/config]}] (thymeleaf/start config))
                                  :config (ds/ref [:env :runtime-config :thymeleaf])}}}})

(defmethod ds/named-system :base
  [_]
  base-system)

(defmethod ds/named-system :local
  [_]
  (ds/system :base {[:env] (load-config :local)}))

(defmethod ds/named-system :example
  [_]
  (ds/system :base {[:env] (load-config :example)}))

(defmethod ds/named-system :test
  [_]
  (ds/system :base {[:env] (load-config :test)
                    [:app-config :jmxmp] ::disabled
                    [:app-config :router] ::disabled
                    [:app-config :runtime-config] (ds/ref [:env :runtime-config])}))

(defmethod ds/named-system :staging
  [_]
  (ds/system :base {[:env] (load-config :staging)}))

(defmethod ds/named-system :production
  [_]
  (ds/system :base {[:env] (load-config :production)}))
