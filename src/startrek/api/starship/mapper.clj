(ns startrek.api.starship.mapper
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(defn request->create
  [request]
  (let [{{{:keys [captain affiliation launched class registry image]} :body} :parameters} request]
    {:captain captain
     :affiliation affiliation
     :launched launched
     :class class
     :registry registry
     :image image}))

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
        {{{:keys [captain affiliation launched class registry image]} :body} :parameters} request
        starship {:id id
                  :captain captain
                  :affiliation affiliation
                  :launched launched
                  :class class
                  :registry registry
                  :image image}]
    starship))

(defn request->search
  [request]
  (let [{{{:keys [id captain affiliation launched class registry image]} :query} :parameters} request
        starship {:id id
                  :captain captain
                  :affiliation affiliation
                  :launched launched
                  :class class
                  :registry registry
                  :image image}]
    starship))
