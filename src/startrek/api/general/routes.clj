(ns startrek.api.general.routes
  {:author "David Harrigan"}
  (:require
   [reitit.swagger :as swagger]
   [startrek.api.general.handler :as handler]))

(set! *warn-on-reflection* true)

(defn routes
  []
  [["/favicon.ico" {:get {:no-doc true
                          :handler handler/favicon}}]
   ["/version" {:get {:no-doc true
                      :handler handler/version}}]
   ["/ping" {:get {:no-doc true
                   :handler handler/ping}}]
   ["/prometheus" {:get {:no-doc true
                         :handler handler/metrics}}]
   ["/docs/*" {:get {:no-doc true
                     :handler handler/swagger-ui}}]
   ["/swagger.json" {:get {:no-doc true
                           :swagger {:info {:title "API Documentation"}
                                     :basePath "/"}
                           :handler (swagger/create-swagger-handler)}}]])
