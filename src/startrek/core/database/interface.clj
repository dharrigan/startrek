(ns startrek.core.database.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.database.impl :as db]))

(set! *warn-on-reflection* true)

(defn execute
  "Execute (and return a single result from) a `sql` side-effecting action on the `datasource`."
  ([datasource sql]
   (execute datasource sql {}))
  ([datasource sql opts]
   (db/execute datasource sql opts)))

(defn select
  "`sql` select many rows from the `datasource`."
  ([datasource sql]
   (select datasource sql {}))
  ([datasource sql opts]
   (db/select datasource sql opts)))

(defn select-many
  "`sql` select many rows from the `datasource`."
  ([datasource sql]
   (select-many datasource sql {}))
  ([datasource sql opts]
   (db/select-many datasource sql opts)))
