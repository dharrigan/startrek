(ns startrek.ui.lcars.starships.render
  {:author "David Harrigan"}
  (:require
   [startrek.core.aux.thymeleaf.interface :as thymeleaf]
   [startrek.ui.utils.response :as response-utils]))

(set! *warn-on-reflection* true)

(defn starships-dashboard
  [request data]
  (-> (thymeleaf/render "lcars/starships/dashboard/index" data request)
      (response-utils/html)))

(defn starship
  [request data]
  (-> (thymeleaf/render "/lcars/starships/details/index" data request)
      (response-utils/html)))
