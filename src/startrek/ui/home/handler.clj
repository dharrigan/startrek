(ns startrek.ui.home.handler
  {:author "David Harrigan"}
  (:require
   [clojure.string :refer [starts-with?]]
   [ring.util.response :as response]
   [startrek.shared.utils.platform :as platform-utils]
   [startrek.ui.home.render :as render]))

(set! *warn-on-reflection* true)

(defn index
  [{:keys [server-name] :as request}]
  (cond
    (starts-with? server-name "lcars") (response/redirect "/lcars/login")
    :else (let [response (response/response {:message (format "World! I am version '%s'." (platform-utils/version))})]
            (render/index request response))))
