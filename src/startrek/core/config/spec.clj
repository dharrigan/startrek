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
                [:password :string]]]
              [:redis {:default "redis://localhost/0"} [:map [:sessions [:map [:uri :string]]]]]]]
   [:runtime-config [:map
                     [:db [:map [:migration-locations {:default ["db/platform/migrations"]} [:vector :string]]]]
                     [:cookies [:map
                                [:secure? {:default false} :boolean]
                                [:session-cookie-name {:default "__HOST-lcars-session"} :string]
                                [:token-cookie-name {:default "__HOST-lcars-token"} :string]]]
                     [:core-control [:map
                                     [:keep-previous-session? {:default false} :boolean]]]
                     [:cors [:map
                             [:allow-origin {:default "http://localhost"} :string]
                             [:allow-headers {:default "*"} :string]
                             [:allow-credentials? {:default true} :boolean]
                             [:allow-methods {:default "CONNECT, DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT"} :string]]]
                     [:environment {:default :local} [:enum :local :test :development :staging :production]]
                     [:jetty {:default 8080} [:map [:port pos-int?]]]
                     [:jmxmp {:default 5555} [:map [:port pos-int?]]]
                     [:thymeleaf [:map
                                  [:prefix {:default "public/"} :string]
                                  [:suffix {:default ".html"} :string]
                                  [:cacheable? {:default false} :boolean]
                                  [:cache-ttl-ms {:default 0} [:or [:= 0] pos-int?]]]]]]])

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
       (apply-defaults))

  (->> (io/resource "config/config-local.edn")
       (read-config)
       (apply-defaults)
       (validate)))
