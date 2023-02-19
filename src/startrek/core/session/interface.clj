(ns startrek.core.session.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.session.impl :as session]))

(set! *warn-on-reflection* true)

(defn delete-session
  [opts app-config]
  (session/delete-session opts app-config))

(defn get-session
  [opts app-config]
  (session/get-session opts app-config))

(defn save-session
  [opts app-config]
  (session/save-session opts app-config))
