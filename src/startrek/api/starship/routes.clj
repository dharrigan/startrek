(ns startrek.api.starship.routes
  {:author "David Harrigan"}
  (:require
   [startrek.api.starship.handler :as handler]
   [startrek.api.starship.specs :as specs]
   [startrek.api.utils.constants :refer [application-json created ok]]))

(set! *warn-on-reflection* true)

(def ^:private starships-create-api-version "application/vnd.startrek.starships.create.v1+json;charset=utf-8")
(def ^:private starships-delete-api-version "application/vnd.startrek.starships.delete.v1+json;charset=utf-8")
(def ^:private starships-find-by-id-api-version "application/vnd.startrek.starships.find-by-id.v1+json;charset=utf-8")
(def ^:private starships-modify-api-version "application/vnd.startrek.starships.modify.v1+json;charset=utf-8")
(def ^:private starships-search-api-version "application/vnd.startrek.starships.search.v1+json;charset=utf-8")

(defn routes
  []
  ["/starships"
   ["" {:get {:handler handler/search
              :parameters {:query specs/search}
              :responses {ok {:body specs/search-starships-response}}
              :swagger {:consumes [application-json]
                        :produces [starships-search-api-version]}}
        :post {:handler handler/create
               :parameters {:body specs/create}
               :responses {created {:body specs/create-starship-response}}
               :swagger {:consumes [application-json]
                         :produces [starships-create-api-version]}}}]
   ["/:id" {:parameters {:path specs/starship-id}}
    ["" {:get {:handler handler/find-by-id
               :responses {ok {:body specs/get-starship-response}}
               :swagger {:consumes [application-json]
                         :produces [starships-find-by-id-api-version]}}
         :delete {:handler handler/delete
                  :responses {ok {:body specs/delete-starship-response}}
                  :swagger {:consumes [application-json]
                            :produces [starships-delete-api-version]}}
         :patch {:handler handler/modify
                 :parameters {:body specs/modify}
                 :responses {ok {:body specs/patch-starship-response}}
                 :swagger {:consumes [application-json]
                           :produces [starships-modify-api-version]}}}]]])
