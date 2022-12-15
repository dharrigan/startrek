(ns startrek.api.middleware.app-config
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(defn ^:private with-app-config
  [handler app-config]
  (fn [request]
    (handler (assoc request :app-config app-config))))

(def app-config-middleware
  {:name ::transaction
   :description "Adds 'app-config' to each request/response."
   :wrap with-app-config})
