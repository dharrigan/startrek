(ns startrek.api.general.handler
  {:author "David Harrigan"}
  (:require
   [iapetos.collector.ring :as ring]
   [reitit.swagger-ui :as swagger-ui]
   [ring.util.response :as response]
   [startrek.shared.middleware.metrics :as metrics]
   [startrek.shared.utils.platform :as platform-utils]))

(set! *warn-on-reflection* true)

(defn favicon
  [_]
  (response/response "Dammit Jim!, I'm an API, not a website!"))

(defn metrics
  [_]
  (ring/metrics-response metrics/registry))

(defn not-found
  [_]
  (response/not-found "Sorry Dave, I'm afraid I can't do that."))

(defn ping
  [_]
  (response/response "Pong!"))

(def swagger-ui
  (swagger-ui/create-swagger-ui-handler {:config {:operationsSorter "alpha"
                                                  :docExpansion "list"
                                                  :validatorUrl nil}}))

(defn version
  [_]
  (response/response (platform-utils/version)))
