(ns startrek.api.middleware.sessions
  {:author "David Harrigan"}
  (:require
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.session.memory :as memory]))

(set! *warn-on-reflection* true)

;; PRIVATE FUNCTIONS AND DEFINITIONS ↓

(defn ^:private with-session
  [handler]
  (wrap-session handler {:store (memory/memory-store)})) ;; a simple store, gets reset on application restart (not really suitable for a load-balanced solution...)

;; PUBLIC FUNCTIONS AND DEFINITIONS ↓

(def sessions-middleware
  {:name ::cors
   :description "Adds session functionality to each request/response."
   :wrap with-session})
