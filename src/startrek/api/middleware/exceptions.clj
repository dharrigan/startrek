(ns startrek.api.middleware.exceptions
  {:author "David Harrigan"}
  (:require
   [clojure.string :refer [split]]
   [clojure.tools.logging :as log]
   [reitit.ring.middleware.exception :as exception]
   [startrek.api.utils.constants :refer [internal-server-error]]
   [startrek.core.utils.i18n :refer [resolve-message]]))

(set! *warn-on-reflection* true)

(def ^:private double-colon (re-pattern "::"))

(defn ^:private split-error-message
  [error]
  (let [code-and-error (split error double-colon)]
    {:code (first code-and-error) :message (str (second code-and-error))}))

(defn ^:private format-application-error
  [error message {:keys [id resource] :as opts}]
  (case error
    :database.unavailable message
    :general.exception message
    :missing.or.invalid.basic.credentials message
    :missing.or.invalid.token.credentials message
    :not.enabled message
    :resource.exists (format message resource id)
    :resource.does.not.exist (format message resource id)
    :service.unavailable message))

(defn ^:private handle-api-error
  [reference locales {:keys [error type reason] :as exception-data}]
  (let [resource-id (keyword (name type) (name error))
        resolved-error-message (resolve-message locales resource-id)
        {:keys [code message]} (split-error-message resolved-error-message)
        resolved-error (format-application-error error message reason)]
    {:code code :reference reference :error resolved-error}))

(defn ^:private exception-info-handler
  [exception-info {:keys [locales] :as request}] ; exception-info and request both come from reitit.
  (let [{:keys [http-status error] :or {http-status 500} :as exception-data} (ex-data exception-info)
        reference (str (random-uuid))
        body (-> (case error
                   ;;
                   ;; add other exception-info types and handlers here ↑ if appropriate
                   ;;
                   ;; finally, catch all.
                   ;;
                   (handle-api-error reference locales exception-data))
                 (assoc :uri (:uri request)))]
    (case error
      :missing.or.invalid.basic.credentials {:status http-status :headers {"WWW-Authenticate" "Basic realm=\"startrek\""} :body body}
      :missing.or.invalid.token.credentials {:status http-status :headers {"WWW-Authenticate" "Token realm=\"startrek\""} :body body}
      {:status http-status :body body})))

(defn ^:private exception-handler
  [error _exception {:keys [locales] :as request}] ; exception and request come from reitit.
  (let [resource-id (keyword "api" (name error))
        resolved-error-message (resolve-message locales resource-id)
        {:keys [code message]} (split-error-message resolved-error-message)
        reference (str (random-uuid))
        body {:code code :error message :reference reference :uri (:uri request)}]
    {:status internal-server-error :body body}))

(def exceptions-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers ;; reitit default handlers
    {clojure.lang.ExceptionInfo exception-info-handler
     java.io.IOException (partial exception-handler :service.unavailable)
     java.lang.IllegalArgumentException (partial exception-handler :service.unavailable)
     java.sql.SQLException (partial exception-handler :database.unavailable)
     ;;
     ;; Catch everything else...
     ;;
     ::exception/default (partial exception-handler :general.exception)
     ;;
     ;; this ↓ wraps every handler, including the retit default handlers,
     ;; exception-info-handler and the exception-handler.
     ;;
     ::exception/wrap (fn [handler exception request]
                        (let [response (handler exception request)]
                          (log/warn response)
                          response))})))
