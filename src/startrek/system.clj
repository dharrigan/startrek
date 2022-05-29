(ns startrek.system
  {:author "David Harrigan"}
  (:require
   [aero.core :refer [read-config]]
   [clojure.java.io :as io]
   [donut.system :as ds]
   [startrek.api.router :as router]
   [startrek.components.database.impl :as db]
   [startrek.components.jmxmp.impl :as jmxmp]))

(set! *warn-on-reflection* true)

(defn ^:private load-config
  [environment]
  (-> (io/resource (str "config/config" (when-not (= :production environment) (str "-" (name environment))) ".edn"))
      (read-config)))

(def ^:private base-system
  {::ds/defs
   {:env {}
    :app {:db #::ds{:start (fn [{:keys [::ds/config]}] (db/start config))
                    :after-start (fn [{:keys [::ds/instance]}] (db/after-start instance ["db/migration/postgresql"]))
                    :stop (fn [{:keys [::ds/instance]}] (db/stop instance))
                    :config (ds/ref [:env :secrets :db])}

          :router #::ds{:start (fn [{:keys [::ds/config]}] (router/start config))
                        :stop (fn [{:keys [::ds/instance]}] (router/stop instance))
                        :config {:db (ds/ref [:app :db])
                                 :runtime-config (ds/ref [:env :runtime-config])}}

          :jmxmp #::ds{:start (fn [{:keys [::ds/config]}] (jmxmp/start config))
                       :stop (fn [{:keys [::ds/instance]}] (jmxmp/stop instance))
                       :config (ds/ref [:env :runtime-config :jmxmp])}}}})

(defmethod ds/named-system :base
  [_]
  base-system)

(defmethod ds/named-system :local
  [_]
  (ds/system :base {[:env] (load-config :local)}))

(defmethod ds/named-system :staging
  [_]
  (ds/system :base {[:env] (load-config :staging)}))

(defmethod ds/named-system :production
  [_]
  (ds/system :base {[:env] (load-config :production)}))
