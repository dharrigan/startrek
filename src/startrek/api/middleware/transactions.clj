(ns startrek.api.middleware.transactions
  {:author "David Harrigan"}
  (:require
   [next.jdbc :as jdbc]))

(set! *warn-on-reflection* true)

(defn ^:private with-transaction
  [handler]
  (fn [{{:keys [db]} :app-config :as request}]
    (jdbc/with-transaction [tx db]
      (handler (assoc-in request [:app-config :db] tx)))))

(def transactions-middleware
  {:name ::transaction
   :description "Creates a new database transaction for each request/response."
   :wrap with-transaction})
