(ns startrek.core.cache.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [taoensso.carmine :as car :refer [wcar]])
  (:import
   [taoensso.carmine.connections ConnectionPool]))

(set! *warn-on-reflection* true)

(def ^:private two-weeks-in-seconds (* 60 60 24 14)) ;; 1209600

(defn redis-del
  [k {{:keys [connection]} :sessions-cache :as app-config}]
  (log/tracef "Deleting key '%s' from redis." k)
  (when-let [result (wcar connection (car/del k))]
    (log/tracef "Deleted key '%s' from redis." k)
    result))

(defn redis-get
  [k {{:keys [connection]} :sessions-cache :as app-config}]
  (log/tracef "Looking up key '%s' in redis." k)
  (when-let [result (wcar connection (car/get k))]
    (log/tracef "Found key '%s', value '%s' in redis." k result)
    result))

(defn redis-keys
  [pattern {{:keys [connection]} :sessions-cache :as app-config}]
  (log/tracef "Looking up keys using pattern '%s' in redis." pattern)
  (when-let [results (wcar connection (car/keys pattern))]
    (log/tracef "Found keys using pattern '%s', values '%s' in redis." pattern results)
    results))

(defn redis-put
  [k v {:keys [expiry-seconds] :or {expiry-seconds two-weeks-in-seconds} :as opts} {{:keys [connection]} :sessions-cache :as app-config}]
  (wcar connection (car/set k v :ex expiry-seconds))
  (log/tracef "Stored key '%s', value '%s' into redis with an expiry of '%s' seconds." k v expiry-seconds)
  v)

;; DONUT LIFECYCLE FUNCTIONS ↓

(defn start
  [{:keys [uri] :as config}]
  {:connection {:pool (car/connection-pool {}) :spec {:uri uri}}})

(defn stop
  [{{:keys [pool]} :connection :as instance}]
  (.close ^ConnectionPool pool))
