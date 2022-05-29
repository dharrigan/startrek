(ns startrek.api.general.swagger
  {:author "David Harrigan"}
  (:require
   [reitit.swagger :as swagger]))

(set! *warn-on-reflection* true)

(def routes
  ["/swagger.json" {:get {:no-doc true
                          :swagger {:info {:title "Star Trek API"}
                                    :basePath "/"}
                          :handler (swagger/create-swagger-handler)}}])
