(ns startrek.core.database.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.database.impl :as db]))

(set! *warn-on-reflection* true)

;;
;; Alphabetical order please!
;;

(defn execute
  "Execute (and return a single result from) a `sql` side-effecting action on the `datasource`."
  ([sql datasource]
   (execute sql datasource {}))
  ([sql datasource opts]
   (db/execute sql datasource opts)))

(defn health-check
  "Perform a health check on the database, basically a `SELECT 1`.
   This is used for doing Prometheus/Grafana/Alert Manager type of monitoring."
  [app-config]
  (db/health-check app-config))

(defn select
  "`sql` select many rows from the `datasource`."
  ([sql datasource]
   (select sql datasource {}))
  ([sql datasource opts]
   (db/select sql datasource opts)))

(defn select-many
  "`sql` select many rows from the `datasource`."
  ([sql datasource]
   (select-many sql datasource {}))
  ([sql datasource opts]
   (db/select-many sql datasource opts)))
