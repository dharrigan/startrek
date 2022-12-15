(ns startrek.api.utils.platform
  {:author "David Harrigan"}
  (:require
   [clojure.java.io :as io]))

(set! *warn-on-reflection* true)

(defn version
  []
  (if-let [version (io/resource "version.txt")] (slurp version) "0.0.1"))
