(ns startrek.shared.middleware.cors
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [ring.util.response :as response]
   [startrek.core.config.interface :as config]))

(set! *warn-on-reflection* true)

(defn ^:private add-cors-headers
  [response {:keys [allow-origin allow-headers allow-credentials? allow-methods] :as cors}]
  (-> response
      (assoc-in [:headers "Access-Control-Allow-Origin"] allow-origin)
      (assoc-in [:headers "Access-Control-Allow-Headers"] allow-headers)
      (assoc-in [:headers "Access-Control-Allow-Credentials"] (str allow-credentials?))
      (assoc-in [:headers "Access-Control-Allow-Methods"] allow-methods)))

(defn ^:private handle-preflight
  [cors]
  (log/infof "Adding preflight headers '%s' to the response" cors)
  (add-cors-headers (response/response nil) cors))

(defn ^:private with-cors
  [handler]
  (fn [{:keys [request-method app-config] :as request}]
    (let [origin (get-in request [:headers "origin"])
          cors (-> (config/cors app-config)
                   (assoc :allow-origin origin))] ;; a more sophisticated solution might have a whitelist of approved origins
      (if (= :options request-method)
        (handle-preflight cors) ;; return early if this is an OPTIONS request, no need to do any further request processing.
        (-> (handler request)
            (add-cors-headers cors))))))

(def cors-middleware
  {:name ::cors
   :description "Adds CORS headers to each request/response."
   :wrap with-cors})
