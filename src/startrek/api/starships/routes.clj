(ns startrek.api.starships.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.starships.handler :as handler]
   [startrek.api.starships.spec :as spec]
   [startrek.shared.constants :refer [application-json created ok]]))

(set! *warn-on-reflection* true)

(def ^:private starships-create-api-version "application/vnd.startrek.starships.create.v1+json;charset=utf-8")
(def ^:private starships-delete-api-version "application/vnd.startrek.starships.delete.v1+json;charset=utf-8")
(def ^:private starships-find-by-id-api-version "application/vnd.startrek.starships.find-by-id.v1+json;charset=utf-8")
(def ^:private starships-modify-api-version "application/vnd.startrek.starships.modify.v1+json;charset=utf-8")
(def ^:private starships-search-api-version "application/vnd.startrek.starships.search.v1+json;charset=utf-8")

(defn private-routes
  []
  ["/starships"
   ["" {:post {:handler handler/create
               :parameters {:body spec/create-starship-request}
               :responses {created {:body spec/create-starship-response}}
               :swagger {:consumes [application-json]
                         :produces [starships-create-api-version]}}}]
   ["/:id" {:parameters {:path spec/starship-id}}
    ["" {:delete {:handler handler/delete
                  :responses {ok {:body spec/delete-starship-response}}
                  :swagger {:consumes [application-json]
                            :produces [starships-delete-api-version]}}
         :patch {:handler handler/modify
                 :parameters {:body spec/patch-starship-request}
                 :responses {ok {:body spec/patch-starship-response}}
                 :swagger {:consumes [application-json]
                           :produces [starships-modify-api-version]}}}]]])

(defn public-routes
  []
  ["/starships"
   ["" {:get {:handler handler/search
              :parameters {:query spec/search-starships-request}
              :responses {ok {:body spec/search-starships-response}}
              :swagger {:consumes [application-json]
                        :produces [starships-search-api-version]}}}]
   ["/:id" {:parameters {:path spec/get-starship-request}}
    ["" {:get {:handler handler/find-by-id
               :responses {ok {:body spec/get-starship-response}}
               :swagger {:consumes [application-json]
                         :produces [starships-find-by-id-api-version]}}}]]])
