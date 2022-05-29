(ns startrek.api.general.health
  {:author "David Harrigan"}
  (:require
   [startrek.api.util.http :as http-util]))

(set! *warn-on-reflection* true)

(def routes
  ["/ping" {:get {:handler (constantly {:status http-util/ok :body "Pong!"})}}])
