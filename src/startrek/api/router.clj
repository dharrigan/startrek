(ns startrek.api.router
  {:author "David Harrigan"}
  (:require
   [camel-snake-kebab.core :as csk]
   [muuntaja.core :as m]
   [reitit.coercion.malli :as rcm]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.spec :as rs]
   [reitit.swagger :as swagger]
   [ring.adapter.jetty :as jetty]
   [startrek.api.general.actuator :as actuator-api]
   [startrek.api.general.favicon :as favicon-api]
   [startrek.api.general.health :as health-api]
   [startrek.api.general.metrics :as metrics]
   [startrek.api.general.swagger :as swagger-api]
   [startrek.api.middleware.app-config :as app-config-middleware]
   [startrek.api.middleware.cors :as cors]
   [startrek.api.middleware.exceptions :as exceptions]
   [startrek.api.starship.routes :as starship-api]
   [startrek.api.util.http :as http-util])
  (:import
   [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

(def ^:private public-routes
  [starship-api/routes])

(def ^:private routes
  [["/api" {:middleware [[metrics/prometheus-middleware]]} public-routes]
   ;;
   ;; put other routes below that are *not* to live under the `/api` endpoint
   ;;
   actuator-api/routes
   swagger-api/routes
   health-api/routes
   favicon-api/routes])

(def ^:private custom-serialization
  (m/create
   (-> m/default-options
       (assoc-in [:formats http-util/application-json :encoder-opts] {:encode-key-fn (comp csk/->kebab-case name)}) ;; clojure -> json
       (assoc-in [:formats http-util/application-json :decoder-opts] {:decode-key-fn (comp keyword csk/->kebab-case)})))) ;; json -> clojure

(defn ^:private router
  [app-config]
  (ring/router
   routes
   {:validate rs/validate
    :data {:app-config app-config
           :coercion rcm/coercion
           :muuntaja custom-serialization
           :middleware [swagger/swagger-feature
                        app-config-middleware/app-config-middleware
                        cors/cors-middleware
                        muuntaja/format-middleware
                        metrics/metrics-middleware
                        exceptions/exceptions-middleware
                        parameters/parameters-middleware
                        coercion/coerce-exceptions-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-response-middleware]}}))

;; Donut Lifecycle Functions

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start
  [{{:keys [jetty]} :runtime-config :as app-config}]
  (jetty/run-jetty
   (ring/ring-handler (router app-config) (ring/create-default-handler))
   (merge jetty {:allow-null-path-info true
                 :send-server-version? false
                 :send-date-header? false
                 :join? false}))) ;; false so that we can stop it at the repl!

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop
  [^Server server]
  (.stop server) ; stop is async
  (.join server)) ; so let's make sure it's really stopped!
