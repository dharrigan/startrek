(ns startrek.api.utils.malli
  {:author "David Harrigan"}
  (:require
   [malli.core :as m]
   [malli.transform :as mt]))

(set! *warn-on-reflection* true)

(defn decode
  [spec body]
  (m/decode spec body mt/strip-extra-keys-transformer))
