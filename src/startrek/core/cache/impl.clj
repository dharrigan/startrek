(ns startrek.core.cache.impl
  {:author "David Harrigan"}
  (:require
   [celtuce.codec :refer [utf8-string-codec]]
   [celtuce.commands :as redis]
   [celtuce.connector :as conn]
   [clojure.tools.logging :as log]
   [jsonista.core :as json])
  (:import
   [io.lettuce.core SetArgs$Builder]))

(set! *warn-on-reflection* true)

(def ^:private strip-nils-mapper (json/object-mapper {:strip-nils true}))
(def ^:private two-weeks-in-ms (* 1000 60 60 24 14)) ;; 1209600000

(defn ^:private map->string
  [coll]
  (json/write-value-as-string coll strip-nils-mapper))

(defn ^:private redis-key-expiry
  [expiry-ms]
  (SetArgs$Builder/px ^long expiry-ms))

(defn redis-del
  [k {{:keys [cmds]} :sessions-cache :as app-config}]
  (log/tracef "Deleting key '%s' from redis." k)
  (when-let [result (redis/del cmds k)]
    (log/tracef "Deleted key '%s' from redis." k)
    result))

(defn redis-get
  [k {{:keys [cmds]} :sessions-cache :as app-config}]
  (log/tracef "Looking up key '%s' in redis." k)
  (when-let [result (redis/get cmds k)]
    (log/tracef "Found key '%s', value '%s' in redis." k result)
    result))

(defn redis-keys
  [pattern {{:keys [cmds]} :sessions-cache :as app-config}]
  (log/tracef "Looking up keys using pattern '%s' in redis." pattern)
  (when-let [results (redis/keys cmds pattern)]
    (log/tracef "Found keys using pattern '%s', values '%s' in redis." pattern results)
    results))

(defn redis-put
  [k v {:keys [expiry-ms] :or {expiry-ms two-weeks-in-ms} :as opts} {{:keys [cmds]} :sessions-cache :as app-config}]
  (log/tracef "Storing key '%s', value '%s' into redis with an expiry of '%s' milliseconds." k v expiry-ms)
  (redis/set cmds k (map->string v) (redis-key-expiry expiry-ms))
  v)

;; DONUT LIFECYCLE FUNCTIONS ↓

(defn start
  [{:keys [uri] :as config}]
  (when-let [connector (conn/redis-server uri :codec (utf8-string-codec))]
    {:connector connector :cmds (conn/commands-sync connector)}))

(defn stop
  [config]
  (conn/shutdown (:connector config)))
