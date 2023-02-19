(ns startrek.core.cache.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.cache.impl :as cache]))

(set! *warn-on-reflection* true)

(def ^:private two-weeks-in-ms (* 1000 60 60 24 14)) ;; 1209600000

(defn redis-del
  [k app-config]
  (cache/redis-del k app-config))

(defn redis-get
  [k app-config]
  (cache/redis-get k app-config))

(defn redis-keys
  [pattern app-config]
  (cache/redis-keys pattern app-config))

(defn redis-put
  ([k v app-config] (redis-put k v {:expiry-ms two-weeks-in-ms} app-config))
  ([k v opts app-config]
   (cache/redis-put k v opts app-config)))
