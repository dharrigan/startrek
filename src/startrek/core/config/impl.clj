(ns startrek.core.config.impl
  {:author "David Harrigan"}
  (:require
   [startrek.core.config.spec :as spec]))

(set! *warn-on-reflection* true)

(defn apply-defaults
  [config]
  (spec/apply-defaults config))

(defn cors
  [{{:keys [cors]} :runtime-config :as app-config}]
  cors)

(defn validate
  [config]
  (spec/validate config))
