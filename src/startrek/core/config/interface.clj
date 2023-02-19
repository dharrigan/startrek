(ns startrek.core.config.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.config.impl :as config]))

(set! *warn-on-reflection* true)

(defn apply-defaults
  [config]
  (config/apply-defaults config))

(defn keep-previous-session?
  [app-config]
  (config/keep-previous-session? app-config))

(defn cookies
  [app-config]
  (config/cookies app-config))

(defn cors
  [app-config]
  (config/cors app-config))

(defn validate
  [config]
  (config/validate config))
