(ns startrek.api.middleware.exceptions
  {:author "David Harrigan"}
  (:require
   [clojure.string :refer [split]]
   [clojure.tools.logging :as log]
   [reitit.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [startrek.api.utils.constants :refer [bad-request internal-server-error]]
   [startrek.core.utils.i18n :refer [resolve-message]]))

(set! *warn-on-reflection* true)

(def ^:private double-colon (re-pattern "::"))

(defn ^:private split-error-message
  [error-message]
  (if (re-find double-colon error-message)
    (let [code-and-error (split error-message double-colon)]
      {:code (first code-and-error) :message (str (second code-and-error))})
    {:code nil :message error-message})) ;; This is an error we don't yet know how to handle, so just return the message with no code.

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
  [locales {:keys [error type reason] :as exception-data}]
  (let [resource-id (keyword (name type) (name error))
        resolved-error-message (resolve-message locales resource-id)
        {:keys [code message]} (split-error-message resolved-error-message)
        resolved-error (format-application-error error message reason)]
    {:code code :reference (random-uuid) :error resolved-error}))

(defn ^:private exception-info-handler
  [exception-info {:keys [locales] :as request}] ; exception-info and request both come from reitit.
  (let [{:keys [http-status error] :or {http-status 500} :as exception-data} (ex-data exception-info)
        body (-> (case error
                   ;;
                   ;; add other exception-info types and handlers here ↑ if appropriate
                   ;;
                   ;; finally, catch all.
                   ;;
                   (handle-api-error locales exception-data))
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
        body {:code code :reference (random-uuid) :error message :uri (:uri request)}]
    {:status internal-server-error :body body}))

(defn ^:private format-coercion-error
  [code message {:keys [value] :as error}]
  (let [in (-> error :in first name)] ;; a bit brittle..., assumes only one element in the `in` vector.
    (if (= "COER10000" code) ;; this is a coercion exception that is thrown where we haven't mapped the keys yet
      (format message in (:message error) value)
      (format message in value))))

(defn ^:private create-coercion-handler
  []
  (fn [exception {:keys [locales] :as request}]
    (let [{:keys [errors]} (coercion/encode-error (ex-data exception))
          {:keys [message] :as error} (first errors)
          resolved-error-message (if (keyword? message)
                                   (resolve-message locales message)
                                   (resolve-message locales :coercion/general.exception))
          {:keys [code message]} (split-error-message resolved-error-message)
          resolved-error (format-coercion-error code message error)
          body {:code code :reference (random-uuid) :error resolved-error :uri (:uri request)}]
      {:status bad-request :body body})))

(def exceptions-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers ;; reitit default handlers
    {;;
     ;; Catch these exceptions and deal using the exception-info-handler (above)
     ;;
     clojure.lang.ExceptionInfo exception-info-handler
     ;;
     ;; Catch these exceptions and deal using the exception handler (above)
     ;;
     java.io.IOException (partial exception-handler :service.unavailable)
     java.lang.IllegalArgumentException (partial exception-handler :service.unavailable)
     java.sql.SQLException (partial exception-handler :database.unavailable)
     ;;
     ::coercion/request-coercion (create-coercion-handler)
     ;;
     ;; Catch everything else...
     ;;
     ::exception/default (partial exception-handler :general.exception)
     ;;
     ;; this ↓ wraps every handler (above), including the retit
     ;; default handlers, exception-info-handler and the exception-handler.
     ;;
     ::exception/wrap (fn [handler exception request]
                        (let [response (handler exception request)]
                          (log/warn response)
                          response))})))
