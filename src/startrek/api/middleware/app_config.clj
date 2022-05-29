(ns startrek.api.middleware.app-config
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def app-config-middleware
  {:name ::app-config
   :description "Adds app-config to each request."
   :compile (fn [{:keys [app-config] :as route-data} _router-opts]
              (fn [handler]
                (fn [request]
                  (handler (assoc request :app-config app-config)))))})
