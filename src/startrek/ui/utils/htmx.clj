(ns startrek.ui.utils.htmx
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]))

(set! *warn-on-reflection* true)

(defn hx-redirect
  [response location]
  (response/header response "HX-Redirect" location))
