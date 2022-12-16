(ns startrek.api.starship.transformations
  {:author "David Harrigan"}
  (:require
   [ring.util.response :refer [not-found response status]]
   [startrek.api.utils.constants :refer [created]]))

(set! *warn-on-reflection* true)

(defn create
  [uuid]
  (-> (response #:starship{:uuid uuid})
      (status created)))

(defn delete
  [{:starship/keys [uuid] :as starship}]
  (response #:starship{:uuid uuid}))

(defn search
  [starships]
  (if (seq starships)
    (response {:starships starships})
    (not-found nil)))

(defn find-by-id
  [starship]
  (if starship
    (response starship)
    (not-found nil)))

(defn modify
  [starship]
  (if starship
    (response starship)
    (not-found nil)))
