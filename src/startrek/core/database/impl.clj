(ns startrek.core.database.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [next.jdbc :as jdbc]
   [next.jdbc.connection :as connection]
   [next.jdbc.date-time] ;; https://github.com/seancorfield/next-jdbc/blob/a568f0fa87b4216941bfefbf3e86f2b182592ff7/src/next/jdbc/date_time.clj#L30
   [next.jdbc.result-set :refer [as-kebab-maps]]
   [startrek.core.database.database-errors :as database-errors :refer [generic-database-error]]
   [startrek.core.database.migration :as migration]
   [startrek.core.errors.interface :as errors :refer [throw-database-exception]])
  (:import
   [com.zaxxer.hikari HikariDataSource]))

(set! *warn-on-reflection* true)

(def ^:private additional-config {:maxLifetime (* 10 60 1000)})

(defn execute
  ([datasource sql] (execute datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute-one! datasource sql (merge {:builder-fn as-kebab-maps} opts))]
       (log/tracef "JDBC Results '%s'." results)
       results)
     (catch Exception exception
       (log/error exception)
       (throw-database-exception (generic-database-error exception sql opts))))))

(defn select
  ([datasource sql] (select datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute-one! datasource sql (merge {:builder-fn as-kebab-maps} opts))]
       (log/tracef "JDBC Result '%s'." results)
       results)
     (catch Exception exception
       (log/error exception)
       (throw-database-exception (generic-database-error exception sql opts))))))

(defn select-many
  ([datasource sql] (select-many datasource sql {}))
  ([datasource sql opts]
   (log/tracef "Executing JDBC '%s'." sql)
   (try
     (when-let [results (jdbc/execute! datasource sql (merge {:builder-fn as-kebab-maps} opts))]
       (log/tracef "JDBC Result '%s'." results)
       results)
     (catch Exception exception
       (log/error exception)
       (throw-database-exception (generic-database-error exception sql opts))))))

;; DONUT LIFECYCLE FUNCTIONS ↓

(defn start ^HikariDataSource
  [{:keys [db] :as config}]
  (connection/->pool HikariDataSource (merge db additional-config)))

(defn post-start
  [^HikariDataSource datasource config]
  (let [{{{:keys [migration-locations]} :db} :runtime-config} config]
    (migration/migrate datasource migration-locations)))

(defn stop
  [^HikariDataSource datasource]
  (.close datasource))
