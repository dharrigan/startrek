(ns startrek.api.middleware.cors
  {:author "David Harrigan"}
  (:require
   [ring.middleware.cors :refer [wrap-cors]]))

(set! *warn-on-reflection* true)

(def cors-middleware
  [wrap-cors
   :access-control-allow-origin [#".*"]
   :access-control-allow-methods [:delete :get :patch :post :put]])
