(ns dev
  {:author "David Harrigan"}
  (:require
   [donut.system :as ds]
   [donut.system.repl :as donut]
   [startrek.system]))

(set! *warn-on-reflection* true)

(defmethod ds/named-system ::ds/repl
  [_]
  (ds/system :local))

(defn go
  []
  (donut/start))

(defn stop
  []
  (donut/stop))

(defn restart
  []
  (donut/restart))
