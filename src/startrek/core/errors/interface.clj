(ns startrek.core.errors.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.errors.impl :as errors]))

(set! *warn-on-reflection* true)

(defn throw-database-exception
  [error]
  (errors/throw-database-exception error))

(defn throw-resource-does-not-exist-exception
  [error]
  (errors/throw-resource-does-not-exist-exception error))

(defn throw-resource-exists-exception
  [error]
  (errors/throw-resource-exists-exception error))

(defn throw-unauthorized-exception
  [error]
  (errors/throw-unauthorized-exception error))
