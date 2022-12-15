(ns startrek.api.starship.routes
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]
   [startrek.api.starship.mapper :as mapper]
   [startrek.api.starship.specs :as specs]
   [startrek.api.starship.transformations :as transformations]
   [startrek.api.utils.constants :refer [application-json]]
   [startrek.core.starship.interface :as starship]))

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
    (-> (response/response uuid)
        (response/status 201))))

(defn ^:private delete
  [{:keys [app-config] :as request}]
  (if-let [uuid (some-> (mapper/request->delete request)
                        (starship/delete app-config)
                        (transformations/delete))]
    (response/response uuid)
    (response/not-found nil)))

(defn ^:private find-by-id
  [{:keys [app-config] :as request}]
  (if-let [starship (some-> (mapper/request->find-by-id request)
                            (starship/find-by-id app-config)
                            (transformations/find-by-id))]
    (response/response starship)
    (response/not-found nil)))

(defn ^:private modify
  [{:keys [app-config] :as request}]
  (if-let [starship (some-> (mapper/request->modify request)
                            (starship/modify app-config)
                            (transformations/modify))]
    (response/response starship)
    (response/not-found nil)))

(defn ^:private search
  [{:keys [app-config] :as request}]
  (if-let [starships (some-> (mapper/request->search request)
                             (starship/search app-config)
                             (transformations/search))]
    (response/response starships)
    (response/not-found nil)))

(defn routes
  []
  ["/starships"
   ["" {:get {:handler search
              :parameters {:query specs/search}
              :swagger {:consumes application-json
                        :produces [starships-search-api-version]}}
        :post {:handler create
               :parameters {:body specs/create}
               :swagger {:consumes application-json
                         :produces [starships-create-api-version]}}}]
   ["/:id" {:parameters {:path specs/starship-id}}
    ["" {:get {:handler find-by-id
               :swagger {:consumes application-json
                         :produces [starships-find-by-id-api-version]}}
         :delete {:handler delete
                  :swagger {:consumes application-json
                            :produces [starships-delete-api-version]}}
         :patch {:handler modify
                 :parameters {:body specs/modify}
                 :swagger {:consumes application-json
                           :produces [starships-modify-api-version]}}}]]])
