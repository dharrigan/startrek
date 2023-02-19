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
   [startrek.api.routes :as api]
   [startrek.shared.constants :refer [application-json]]
   [startrek.shared.middleware.app-config :as app-config]
   [startrek.shared.middleware.cors :as cors]
   [startrek.shared.middleware.exceptions :as exceptions]
   [startrek.shared.middleware.headers :as headers]
   [startrek.shared.middleware.locales :as locales]
   [startrek.shared.middleware.metrics :as metrics]
   [startrek.shared.middleware.query-string :as query-string]
   [startrek.shared.middleware.sessions :as sessions]
   [startrek.shared.middleware.transactions :as transactions]
   [startrek.shared.middleware.ui-exceptions :as ui-exceptions]
   [startrek.ui.routes :as ui])
  (:import
   [org.eclipse.jetty.server Server]))

(set! *warn-on-reflection* true)

(defn ^:private routes
  []
  [;;
   ;; User Interface
   ;;
   ["" {:tag :ui :swagger {:tags ["User Interface"]} :no-doc true} (ui/routes)]
   ;;
   ;; Startrek API
   ;;
   ["" {:tag :api :swagger {:tags ["Startrek API"]}} (api/routes)]
   ;;
   ;; put other routes below that are *not* to live under the `/api` endpoint
   ;;
   ["" {:tag :general :swagger {:tags ["General Routes"]} :no-doc true} (general/routes)]])

(def ^:private custom-serialization
  (m/create
   (-> m/default-options
       (assoc-in [:formats application-json :encoder-opts] {:encode-key-fn (comp csk/->camelCase name) :strip-nils true}) ;; clojure -> json
       (assoc-in [:formats application-json :decoder-opts] {:decode-key-fn (comp keyword csk/->kebab-case)})))) ;; json -> clojure

(defn ^:private router
  [app-config]
  (ring/router
   (routes)
   {:validate rs/validate
    :data {:app-config app-config
           :coercion rcm/coercion
           :muuntaja custom-serialization
           :middleware [swagger/swagger-feature
                        cors/cors-middleware
                        muuntaja/format-middleware
                        metrics/metrics-middleware
                        ui-exceptions/ui-exceptions-middleware
                        exceptions/exceptions-middleware
                        parameters/parameters-middleware
                        coercion/coerce-request-middleware
                        coercion/coerce-response-middleware
                        cookies/wrap-cookies
                        headers/headers-middleware
                        query-string/query-string-middleware
                        transactions/transactions-middleware]}}))

(defn ^:private static-ring-handler
  [app-config]
  (ring/ring-handler (router app-config)
                     (ring/routes
                      (ring/create-resource-handler {:path "/" :not-found-handler general-handler/not-found})
                      (ring/create-default-handler))
                     {:middleware [[app-config/app-config-middleware app-config] ;; theses middlewares are applied before any other middleware. They are "global" so to speak...
                                   [locales/locales-middleware]
                                   [sessions/sessions-middleware app-config]]}))

(defn ^:private repl-friendly-ring-handler
  [app-config]
  (fn [request]
    ((static-ring-handler app-config) request)))

;; DONUT LIFECYCLE FUNCTIONS ↓

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
