(ns startrek.ui.routes
  {:author "David Harrigan"}
  (:require
   [startrek.ui.home.routes :as home-ui]
   [startrek.ui.lcars.routes :as lcars-ui]))

(set! *warn-on-reflection* true)

(defn routes
  []
  [(home-ui/routes)
   (lcars-ui/routes)])
