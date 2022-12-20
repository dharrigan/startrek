(ns startrek.api.starships.transformations
  {:author "David Harrigan"}
  (:require
   [ring.util.response :refer [not-found response status]]
   [startrek.api.utils.constants :refer [created]]
   [startrek.core.errors.interface :refer [throw-resource-does-not-exist-exception]]))

(set! *warn-on-reflection* true)

(defn ^:private starship-with-id-does-not-exist
  [{:keys [id] :as query}]
  {:message (format "Starship '%s' does not exist!" id) :data {:error :resource.does.not.exist :opts {:resource "starship" :id id}}})

(defn create
  [uuid]
  (-> (response #:starship{:uuid uuid})
      (status created)))

(defn delete
  [query {:starship/keys [uuid] :as starship}]
  (if starship
    (response #:starship{:uuid uuid})
    (throw-resource-does-not-exist-exception (starship-with-id-does-not-exist query))))

(defn search
  [starships]
  (if starships
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
