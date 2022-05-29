(ns startrek.api.general.metrics
  {:author "David Harrigan"}
  (:require
   [iapetos.collector.jvm :as jvm]
   [iapetos.collector.ring :as ring]
   [iapetos.core :as prometheus]))

(set! *warn-on-reflection* true)

(defonce registry
  (-> (prometheus/collector-registry)
      (jvm/initialize)
      (ring/initialize)))

(def ^:private uri-matcher (re-pattern "/api/(starships).*"))
(def ^:private request-methods #{:get :post :delete :patch})

(defn ^:private normalise-path
  [{:keys [request-method uri] :as request}]
  (let [match (last (re-find uri-matcher uri))]
    (if (and match (contains? request-methods request-method))
      (str "/api/" match)
      uri)))

(def metrics
  (fn [_]
    (ring/metrics-response registry)))

(def prometheus-middleware
  {:name ::prometheus
   :description "Adds Prometheus metrics to each request/response"
   :wrap (fn [handler] (ring/wrap-metrics handler registry {:path-fn normalise-path}))})

(def metrics-middleware
  {:name ::metrics
   :description "Adds metrics to each request/response"
   :wrap (fn [handler]
           (ring/wrap-metrics handler registry {:path-fn normalise-path}))})
