(ns startrek.api.middleware.cors
  {:author "David Harrigan"}
  (:require
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

(defn ^:private with-cors
  [handler]
  (fn [{:keys [request-method app-config] :as request}]
    (if (= :options request-method)
      (let [cors (config/cors app-config)]
        (add-cors-headers (response/response nil) cors))
      (handler request))))

(def cors-middleware
  {:name ::cors
   :description "Adds CORS headers to each request/response."
   :wrap with-cors})
