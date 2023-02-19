(ns startrek.ui.utils.response
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]
   [startrek.shared.constants :as constants]))

(set! *warn-on-reflection* true)

(defn html
  [body]
  (-> (response/response body)
      (response/header constants/content-type constants/html)))
