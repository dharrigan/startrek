(ns startrek.api.starship.transformations
  {:author "David Harrigan"}
  (:require
   [clojure.set :as s]))

(set! *warn-on-reflection* true)

(def transform-starships
  (comp
   (map #(s/rename-keys % {:starship/affiliation :affiliation
                           :starship/captain :captain
                           :starship/class :class
                           :starship/created :created
                           :starship/image :image
                           :starship/launched :launched
                           :starship/registry :registry
                           :starship/uuid :id}))
   (map #(dissoc % :starship/starship_id))))

(defn create
  [uuid]
  {:id uuid})

(defn delete
  [uuid]
  (create uuid))

(defn search
  [starships]
  (into [] transform-starships starships))

(defn find-by-id
  [starship]
  (search [starship]))

(defn modify
  [starship]
  (search [starship]))
