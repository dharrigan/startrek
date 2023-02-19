(ns startrek.ui.lcars.starships.routes
  {:author "David Harrigan"}
  (:require
   [startrek.ui.lcars.starships.handler :as handler]
   [startrek.ui.lcars.starships.spec :as spec]))

(set! *warn-on-reflection* true)

(defn private-routes
  []
  [["/starships"
    ["" {:get {:handler handler/starships-dashboard}}]
    ["/:id" {:get {:handler handler/get-starship
                   :parameters {:path spec/get-starship-request}}}]]])
