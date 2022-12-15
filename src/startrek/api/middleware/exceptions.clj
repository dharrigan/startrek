(ns startrek.api.middleware.exceptions
  {:author "David Harrigan"}
  (:require
   [clojure.string :refer [split]]
   [clojure.tools.logging :as log]
   [reitit.ring.middleware.exception :as exception]
   [startrek.api.middleware.i18n :as i18n]
   [taoensso.tempura :as tempura]))

(set! *warn-on-reflection* true)

(def ^:private double-colon (re-pattern "::"))
(def ^:private i18n (partial tempura/tr {:dict i18n/dictionary} [:en]))

(defn ^:private split-error-message
  [error]
  (let [code-and-error (split error double-colon)]
    {:code (first code-and-error) :message (str (second code-and-error))}))

(defn ^:private rekey-http-client-status
  [^long status]
  (case status
    400 :http.400
    401 :http.401
    403 :http.403
    409 :http.409
    404 :http.404
    500 :http.500
    :service.unavailable))

(defn ^:private handle-http-client-error
  [reference {:keys [status body] :as exception-data}]
  (let [error (rekey-http-client-status status)
        {:keys [code message]} (split-error-message (i18n [error]))]
    {:code code :reference reference :error (format message body)}))

(defn ^:private format-application-error
  [error message _]
  (case error
    :database.unavailable message
    :general.exception message
    :not.enabled message
    :service.unavailable message))

(defn ^:private handle-application-error
  [reference {:keys [reason error] :or {error :service.unavailable} :as exception-data}]
  (let [{:keys [code message]} (split-error-message (i18n [error]))]
    {:code code :reference reference :error (format-application-error error message reason)}))

(defn ^:private exception-info-handler
  [exception-info request] ; exception-info and request (which is not-used) both come from reitit.
  (let [{:keys [http-status error] :or {http-status 500} :as exception-data} (ex-data exception-info)
        reference (str (random-uuid))
        body (-> (case error
                   :clj-http.client/unexceptional-status (handle-http-client-error reference exception-data)
                   ;;
                   ;; add other exception-info types and handlers here ↑ if appropriate
                   ;;
                   ;; finally, catch all.
                   ;;
                   (handle-application-error reference exception-data))
                 (assoc :uri (:uri request)))]
    {:status http-status :body body}))

(defn ^:private exception-handler
  [error _exception request] ; exception and request come from reitit.
  (let [{:keys [code message]} (split-error-message (i18n [error]))
        reference (str (random-uuid))
        body {:code code :error message :reference reference :uri (:uri request)}]
    {:status 500 :body body}))

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
