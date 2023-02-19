(ns startrek.api.officers.client
  {:author "David Harrigan"}
  (:require
   [startrek.api.officers.handler :as handler]))

(set! *warn-on-reflection* true)

(defn logout
  [request]
  (handler/logout request))
