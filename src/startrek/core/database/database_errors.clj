(ns startrek.core.database.database-errors
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(defn generic-database-error
  [exception sql opts]
  {:message (ex-message exception) :data {:sql sql :opts opts :error :database.unavailable}})
