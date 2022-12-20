(ns startrek.api.general.routes
  {:author "David Harrigan"}
  (:require
   [reitit.swagger :as swagger]
   [startrek.api.general.handler :as handler]))

(set! *warn-on-reflection* true)

(defn routes
  []
  [["/favicon.ico" {:get {:handler handler/favicon}}]
   ["/version" {:get {:handler handler/version}}]
   ["/ping" {:get {:handler handler/ping}}]
   ["/prometheus" {:get {:handler handler/metrics}}]
   ["/docs/*" {:get {:handler handler/swagger-ui}}]
   ["/swagger.json" {:get {:swagger {:info {:title "API Documentation"} :basePath "/"}
                           :handler (swagger/create-swagger-handler)}}]])
