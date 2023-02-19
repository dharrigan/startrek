(ns startrek.api.starships.client
  {:author "David Harrigan"}
  (:require
   [startrek.api.starships.handler :as handler]
   [startrek.api.starships.spec :as spec]
   [startrek.api.utils.malli :as malli-utils :refer [decode]]))

(defn find-starships
  [request]
  (let [{:keys [body] :as response} (handler/search request)]
    (decode spec/search-starships-response body)))

(defn get-starship
  [request]
  (let [{:keys [body] :as response} (handler/find-by-id request)]
    (decode spec/get-starship-response body)))
