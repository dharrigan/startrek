(ns dev
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [donut.system :as ds]
   [donut.system.repl :as donut]
   [donut.system.repl.state :as state]
   [startrek.system]) ;; required in order to load in the defmulti's that define the donut `named-system`'s.
  (:import
   [clojure.lang ExceptionInfo]))

(set! *warn-on-reflection* true)

(def ^:private environment (atom nil))

(defmethod ds/named-system ::ds/repl
  [_]
  (ds/system @environment))

(defn go
  ([] (go :local))
  ([env]
   (reset! environment env)
   (try
    (donut/start)
    :ready-to-rock-and-roll
    (catch ExceptionInfo e
      (log/error (ex-data e))
      :bye-bye))))

(defn stop
  []
  (donut/stop)
  :bye-bye)

(defn reset
  []
  (donut/restart))

(defn app-config
  []
  (:app-config (::ds/instances state/system)))

(defn runtime-config
  []
  (:env (::ds/instances state/system)))
