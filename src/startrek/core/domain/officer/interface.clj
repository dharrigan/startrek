(ns startrek.core.domain.officer.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.domain.officer.impl :as officer]))

(set! *warn-on-reflection* true)

(defn find-officer
  [query app-config]
  (officer/find-officer query app-config))
