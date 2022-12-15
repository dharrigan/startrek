(ns startrek.api.middleware.metrics
  {:author "David Harrigan"}
  (:require
   [iapetos.collector.jvm :as jvm]
   [iapetos.collector.ring :as ring]
   [iapetos.core :as prometheus]))

(set! *warn-on-reflection* true)

(def ^:private uri-matcher (re-pattern "/api/.*"))
(def ^:private request-methods #{:delete :get :patch :post :put})

(defn ^:private normalise-path
  [{:keys [request-method uri] :as request}]
  (let [match (last (re-find uri-matcher uri))]
    (if (and match (contains? request-methods request-method))
      (str "/api/" match)
      uri)))

(defonce registry
  (-> (prometheus/collector-registry)
      (jvm/initialize)
      (ring/initialize)))

(defn ^:private with-metrics
  [handler]
  (ring/wrap-metrics handler registry {:path-fn normalise-path}))

(def metrics-middleware
  {:name ::metrics
   :description "Adds prometheus metrics to each API request/response."
   :wrap with-metrics})
