(ns startrek.ui.home.render
  {:author "David Harrigan"}
  (:require
   [startrek.core.aux.thymeleaf.interface :as thymeleaf]
   [startrek.ui.utils.response :as response-utils]))

(set! *warn-on-reflection* true)

(defn index
  [request {:keys [body] :as response}]
  (-> (thymeleaf/render "home/index" body request)
      (response-utils/html)))
