(ns startrek.components.starship.interface
  {:author "David Harrigan"}
  (:require
   [startrek.components.starship.impl :as starship]))

(set! *warn-on-reflection* true)

;;
;; Alphabetical order please!
;;

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
