(ns startrek.api.starships.handler
  {:author "David Harrigan"}
  (:require
   [startrek.api.starships.mapper :as mapper]
   [startrek.api.starships.transformations :as transformations]
   [startrek.core.domain.starship.interface :as starship]))

(set! *warn-on-reflection* true)

(defn create
  [{:keys [app-config] :as request}]
  (let [starship (mapper/request->create request)]
    (-> (starship/create starship app-config)
        (transformations/create))))

(defn delete
  [{:keys [app-config] :as request}]
  (let [query (mapper/request->delete request)]
    (->> (starship/delete query app-config)
         (transformations/delete query))))

(defn find-by-id
  [{:keys [app-config] :as request}]
  (let [query (mapper/request->find-by-id request)]
    (-> (starship/find-by-id query app-config)
        (transformations/find-by-id))))

(defn modify
  [{:keys [app-config] :as request}]
  (let [starship (mapper/request->modify request)]
    (-> (starship/modify starship app-config)
        (transformations/modify))))

(defn search
  [{:keys [app-config] :as request}]
  (let [query (mapper/request->search request)]
    (-> (starship/search query app-config)
        (transformations/search))))
