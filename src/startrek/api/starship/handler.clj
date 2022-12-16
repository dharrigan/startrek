(ns startrek.api.starship.handler
  (:require
   [startrek.api.starship.mapper :as mapper]
   [startrek.api.starship.transformations :as transformations]
   [startrek.core.domain.starship.interface :as starship]))

(defn create
  [{:keys [app-config] :as request}]
  (let [starship (mapper/request->create request)]
    (-> (starship/create starship app-config)
        (transformations/create))))

(defn delete
  [{:keys [app-config] :as request}]
  (let [uuid (mapper/request->delete request)]
    (-> (starship/delete uuid app-config)
        (transformations/delete))))

(defn find-by-id
  [{:keys [app-config] :as request}]
  (let [starship (mapper/request->find-by-id request)]
    (-> (starship/find-by-id starship app-config)
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
