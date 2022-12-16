(ns startrek.core.domain.starship.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.domain.starship.impl :as starship]))

(set! *warn-on-reflection* true)

(defn create
  [starship app-config]
  (starship/create starship app-config))

(defn delete
  [query app-config]
  (starship/delete query app-config))

(defn find-by-id
  [query app-config]
  (starship/find-by-id query app-config))

(defn modify
  [query app-config]
  (starship/modify query app-config))

(defn search
  [query app-config]
  (starship/search query app-config))
