(ns startrek.ui.home.routes
  {:author "David Harrigan"}
  (:require
   [startrek.ui.home.handler :as handler]))

(set! *warn-on-reflection* true)

(defn ^:private public-routes
  []
  ["" {:get {:handler handler/index}}])

(defn routes
  []
  ["/" (public-routes)])
