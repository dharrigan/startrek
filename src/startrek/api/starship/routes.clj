(ns startrek.api.starship.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.starship.mapper :as mapper]
   [startrek.api.starship.specs :as specs]
   [startrek.api.starship.transformations :as transformations]
   [startrek.api.util.http :as http-util]
   [startrek.components.starship.interface :as starship]))

(set! *warn-on-reflection* true)

(def ^:private starships-create-api-version "application/vnd.startrek.starships.create.v1+json;charset=utf-8")
(def ^:private starships-delete-api-version "application/vnd.startrek.starships.delete.v1+json;charset=utf-8")
(def ^:private starships-find-by-id-api-version "application/vnd.startrek.starships.find-by-id.v1+json;charset=utf-8")
(def ^:private starships-search-api-version "application/vnd.startrek.starships.search.v1+json;charset=utf-8")
(def ^:private starships-modify-api-version "application/vnd.startrek.starships.modify.v1+json;charset=utf-8")

(defn ^:private create
  [{:keys [app-config] :as request}]
  (when-let [uuid (some-> (mapper/request->create request)
                          (starship/create app-config)
                          (transformations/create))]
    {:status http-util/created :body uuid}))

(defn ^:private delete
  [{:keys [app-config] :as request}]
  (if-let [uuid (some-> (mapper/request->delete request)
                        (starship/delete app-config)
                        (transformations/delete))]
    {:status http-util/ok :body uuid}
    {:status http-util/not-found}))

(defn ^:private find-by-id
  [{:keys [app-config] :as request}]
  (if-let [starship (some-> (mapper/request->find-by-id request)
                            (starship/find-by-id app-config)
                            (transformations/find-by-id))]
    {:status http-util/ok :body starship}
    {:status http-util/not-found}))

(defn ^:private modify
  [{:keys [app-config] :as request}]
  (if-let [starship (some-> (mapper/request->modify request)
                            (starship/modify app-config)
                            (transformations/modify))]
    {:status http-util/ok :body starship}
    {:status http-util/not-found}))

(defn ^:private search
  [{:keys [app-config] :as request}]
  (if-let [starships (some-> (mapper/request->search request)
                             (starship/search app-config)
                             (transformations/search))]
    {:status http-util/ok :body starships}
    {:status http-util/not-found}))

(def routes
  ["/starships"
   ["" {:get {:handler search
              :parameters {:query specs/search}
              :swagger {:consumes http-util/application-json
                        :produces [starships-search-api-version]}}
        :post {:handler create
               :parameters {:body specs/create}
               :swagger {:consumes http-util/application-json
                         :produces [starships-create-api-version]}}}]
   ["/:id" {:parameters {:path specs/starship-id}}
    ["" {:get {:handler find-by-id
               :swagger {:consumes http-util/application-json
                         :produces [starships-find-by-id-api-version]}}
         :delete {:handler delete
                  :swagger {:consumes http-util/application-json
                            :produces [starships-delete-api-version]}}
         :patch {:handler modify
                 :parameters {:body specs/modify}
                 :swagger {:consumes http-util/application-json
                           :produces [starships-modify-api-version]}}}]]])
