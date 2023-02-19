(ns startrek.ui.lcars.starships.handler
  {:author "David Harrigan"}
  (:require
   [startrek.api.starships.client :as starships-client]
   [startrek.ui.lcars.starships.render :as render]))

(set! *warn-on-reflection* true)

(defn starships-dashboard
  [request]
  (let [data (starships-client/find-starships request)]
    (render/starships-dashboard request data)))

(defn get-starship
  [request]
  (let [data (starships-client/get-starship request)]
    (render/starship request data)))
