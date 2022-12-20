(ns startrek.api.starships.mapper
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(defn request->create
  [request]
  (let [{{:keys [body]} :parameters} request]
    body))

(defn request->delete
  [request]
  (let [{{{:keys [id]} :path} :parameters} request]
    {:id id}))

(defn request->find-by-id
  [request]
  (let [{{{:keys [id]} :path} :parameters} request]
    {:id id}))

(defn request->modify
  [request]
  (let [{{{:keys [id]} :path} :parameters} request
        {{:keys [body]} :parameters} request]
    (assoc body :id id)))

(defn request->search
  [request]
  (let [{{:keys [query]} :parameters} request]
    query))
