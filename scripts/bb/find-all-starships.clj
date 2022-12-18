#!/usr/bin/env bb
;;
;;
;;
(require '[cheshire.core :as json])
(require '[org.httpkit.client :as http])

(defn find-all-starships
  []
  (let [{:keys [body]} @(http/get "http://localhost:8080/api/starships")]
    (json/parse-string body true)))

(find-all-starships)
