(ns startrek.test.system
  {:author "David Harrigan"}
  #_{:clj-kondo/ignore [:unused-namespace]}
  (:require
   [donut.system :as ds]
   [startrek.system :as system])) ;; required in order to load in the defmulti's that define the donut `named-system`'s.

(set! *warn-on-reflection* true)

(defn start
  []
  (ds/start :test))

(defn stop
  [system]
  (ds/stop system))
