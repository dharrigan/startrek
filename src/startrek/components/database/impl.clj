(ns startrek.components.database.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection]
   [startrek.components.database.migration :as migration])
  (:import
   [com.zaxxer.hikari HikariDataSource]))

(set! *warn-on-reflection* true)

(def ^:private additional-config {:maxLifetime (* 10 60 1000)})

(defn execute
  ([sql datasource] (execute sql datasource {}))
  ([sql datasource opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
    (when-let [results (jdbc/execute-one! datasource sql opts)]
      (log/tracef "JDBC Results '%s'." results)
      results)
    (catch Exception e
      (log/error e)
      (throw e)))))

(defn select
  ([sql datasource] (select sql datasource {}))
  ([sql datasource opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
    (when-let [results (jdbc/execute-one! datasource sql opts)]
      (log/tracef "JDBC Result '%s'." results)
      results)
    (catch Exception e
      (log/error e)
      (throw e)))))

(defn select-many
  ([sql datasource] (select-many sql datasource {}))
  ([sql datasource opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
    (when-let [results (jdbc/execute! datasource sql opts)]
      (log/tracef "JDBC Result '%s'." results)
      results)
    (catch Exception e
      (log/error e)
      (throw e)))))

(defn health-check
  [{:keys [db] :as app-config}]
  (when-let [results (select (-> (helpers/select 1)
                                 sql/format) db)]
    results))

;; Donut Lifecycle Functions

(defn start
  "Start a connection pool to the datsource defined in the `config`."
  ^HikariDataSource [config]
  (connection/->pool HikariDataSource (merge config additional-config)))

(defn post-start
  "Migrate the database."
  [^HikariDataSource datasource migration-locations]
  (migration/migrate datasource migration-locations))

(defn stop
  "Stop the connection poool for the specified `datasource."
  [^HikariDataSource datasource]
  (.close datasource))
