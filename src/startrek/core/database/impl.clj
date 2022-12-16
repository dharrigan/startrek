(ns startrek.core.database.impl
  {:author "David Harrigan"}
  (:require
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as csk-extras]
   [clojure.tools.logging :as log]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection]
   [next.jdbc.date-time] ;; https://github.com/seancorfield/next-jdbc/blob/a568f0fa87b4216941bfefbf3e86f2b182592ff7/src/next/jdbc/date_time.clj#L30
   [next.jdbc.result-set :refer [as-kebab-maps]]
   [next.jdbc.sql :as jdbc-sql]
   [startrek.core.database.migration :as migration])
  (:import
   [com.zaxxer.hikari HikariDataSource]))

(set! *warn-on-reflection* true)

(def ^:private additional-config {:maxLifetime (* 10 60 1000)})

(defn execute
  ([datasource sql] (execute datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute-one! datasource sql opts)]
       (log/tracef "JDBC Results '%s'." results)
       results)
     (catch Exception e
       (log/error e)
       (throw e)))))

(defn insert
  [datasource table data opts]
  (log/tracef "Inserting '%s into '%s'." data table)
  (try
    (let [data' (csk-extras/transform-keys csk/->snake_case_keyword data)]
      (when-let [results (jdbc-sql/insert! datasource (csk/->snake_case_keyword table) data' (merge {:builder-fn as-kebab-maps} opts))]
        (log/tracef "JDBC result '%s'." results)
        results))
    (catch Exception exception
      (log/error exception)
      (throw exception))))

(defn select
  ([datasource sql] (select datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute-one! datasource sql opts)]
       (log/tracef "JDBC Result '%s'." results)
       results)
     (catch Exception e
       (log/error e)
       (throw e)))))

(defn select-many
  ([datasource sql] (select-many datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute! datasource sql opts)]
       (log/tracef "JDBC Result '%s'." results)
       results)
     (catch Exception e
       (log/error e)
       (throw e)))))

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
