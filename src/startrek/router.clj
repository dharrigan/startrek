(ns startrek.router
  {:author "David Harrigan"}
  (:require
   [camel-snake-kebab.core :as csk]
   [clojure.tools.logging :as log]
   [muuntaja.core :as m]
   [reitit.coercion.malli :as rcm]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.spec :as rs]
   [reitit.swagger :as swagger]
   [ring.adapter.jetty9 :as jetty]
   [ring.middleware.cookies :as cookies]
   [startrek.api.general.handler :as general-handler]
   [startrek.api.general.routes :as general]
   [startrek.api.middleware.app-config :as app-config]
   [startrek.api.middleware.cors :as cors]
   [startrek.api.middleware.exceptions :as exceptions]
   [startrek.api.middleware.metrics :as metrics]
   [startrek.api.middleware.sessions :as sessions]
   [startrek.api.middleware.transactions :as transactions]
   [startrek.api.routes :as api]
   [startrek.api.utils.constants :refer [application-json]])
  (:import
   [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

(defn ^:private routes
  []
  [["" {:swagger {:tags ["Startrek API"]}} (api/routes)]
   ;;
   ;; put other routes below that are *not* to live under the `/api` endpoint
   ;;
   ["" {:no-doc true} (general/routes)]])

(def ^:private custom-serialization
  (m/create
   (-> m/default-options
       (assoc-in [:formats application-json :encoder-opts] {:encode-key-fn (comp csk/->camelCase name) :strip-nils true}) ;; clojure -> json
       (assoc-in [:formats application-json :decoder-opts] {:decode-key-fn (comp keyword csk/->kebab-case)})))) ;; json -> clojure

(defn ^:private router
  []
  (ring/router
   (routes)
   {:validate rs/validate
    :data {:coercion rcm/coercion
           :muuntaja custom-serialization
           :middleware [swagger/swagger-feature
                        cors/cors-middleware
                        muuntaja/format-middleware
                        metrics/metrics-middleware
                        exceptions/exceptions-middleware
                        parameters/parameters-middleware
                        coercion/coerce-exceptions-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-response-middleware
                        cookies/wrap-cookies
                        transactions/transactions-middleware]}}))

(defn ^:private static-ring-handler
  [app-config]
  (ring/ring-handler (router)
                     (ring/routes
                      (ring/create-resource-handler {:path "/" :not-found-handler general-handler/not-found})
                      (ring/create-default-handler))
                     {:middleware [[app-config/app-config-middleware app-config] ;; theses middlewares are applied before any other middleware. They are "global" so to speak...
                                   [sessions/sessions-middleware]]}))

(defn ^:private repl-friendly-ring-handler
  [app-config]
  (fn [request]
    ((static-ring-handler app-config) request)))

;; Donut Lifecycle Functions

(defn start
  [{{:keys [environment jetty]} :runtime-config :as app-config}]
  (jetty/run-jetty
   (if (contains? #{:local :development} environment)
     (do
       (log/infof "Using reloadable ring handler for handling requests as the environment is '%s'." (name environment))
       (repl-friendly-ring-handler app-config))
     (do
       (log/infof "Using static ring handler for handling requests as the environment is '%s'." (name environment))
       (static-ring-handler app-config)))
   (merge jetty {:allow-null-path-info true
                 :send-server-version? false
                 :send-date-header? false
                 :join? false}))) ;; false so that we can stop it at the repl!

(defn stop
  [^Server server]
  (.stop server) ; stop is async
  (.join server)) ; so let's make sure it's really stopped!
