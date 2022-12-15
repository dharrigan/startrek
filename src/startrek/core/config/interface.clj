(ns startrek.core.config.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.config.impl :as config]))

(set! *warn-on-reflection* true)

(defn apply-defaults
  [config]
  (config/apply-defaults config))

(defn cors
  [app-config]
  (config/cors app-config))

(defn validate
  [config]
  (config/validate config))
