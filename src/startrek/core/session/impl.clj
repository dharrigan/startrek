(ns startrek.core.session.impl
  {:author "David Harrigan"}
  (:require
   [startrek.core.session.repository :as repository]))

(set! *warn-on-reflection* true)

(defn delete-session
  [opts app-config]
  (repository/delete-session opts app-config))

(defn get-session
  [opts app-config]
  (repository/get-session opts app-config))

(defn save-session
  [opts app-config]
  (repository/save-session opts app-config))
