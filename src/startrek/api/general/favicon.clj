(ns startrek.api.general.favicon
  {:author "David Harrigan"}
  (:require
   [startrek.api.util.http :as http-util]))

(set! *warn-on-reflection* true)

(def routes
  ["/favicon.ico" {:get {:handler (constantly {:status http-util/ok :body "I'm an API, not a website!"})}}])
