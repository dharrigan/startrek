(ns startrek.core.config.spec
  {:author "David Harrigan"}
  (:require
   [malli.core :as m]
   [malli.error :as me]
   [malli.transform :as mt]
   [malli.util :as mu]))

(set! *warn-on-reflection* true)

(def ^:private Config
  [:map
   [:secrets [:map
              [:db
               [:map
                [:dbtype {:default "postgresql"} [:= "postgresql"]]
                [:dbname {:default "startrek"} [:or [:= "startrek"] [:= "startrek_test"]]]
                [:host {:default "localhost"} :string]
                [:port {:default 5432} pos-int?]
                [:username :string]
                [:password :string]]]]]
   [:runtime-config [:map
                     [:cors [:map
                             [:allow-origin {:default "localhost"} :string]
                             [:allow-headers {:default "*"} :string]
                             [:allow-credentials? {:default true} :boolean]
                             [:allow-methods {:default "CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT"} :string]]]
                     [:environment {:default :local} [:enum :local :test :development :staging :production]]
                     [:jetty {:default 8080} [:map [:port pos-int?]]]
                     [:jmxmp {:default 5555} [:map [:port pos-int?]]]]]])

(defn apply-defaults
  [config]
  (m/decode Config config mt/default-value-transformer))

(defn validate
  [config]
  (-> (mu/closed-schema Config)
      (m/explain config)
      (me/humanize)))

(comment

  (require '[clojure.java.io :as io]
           '[aero.core :refer [read-config]])

  (->> (io/resource "config/config-local.edn")
       (read-config)
       (apply-defaults)
       (validate)))
