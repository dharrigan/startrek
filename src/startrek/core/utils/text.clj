(ns startrek.core.utils.text
  {:author "David Harrigan"}
  (:require
   [buddy.core.codecs :as codecs]
   [buddy.core.hash :as hash]
   [buddy.core.nonce :as nonce]))

(set! *warn-on-reflection* true)

(defn random-id
  []
  (-> (nonce/random-bytes 32)
      (hash/sha256)
      (codecs/bytes->hex)
      (str)))
