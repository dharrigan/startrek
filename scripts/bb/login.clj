#!/usr/bin/env bb
;;
;;
;;
(require '[cheshire.core :as json])
(require '[org.httpkit.client :as http])

(def riker "riker@starship-enterprise.outer.space")
(def password "password")

(defn login
  []
  (let [{:keys [body] :as response} @(http/post (str "http://localhost:8080/api/public/officers/login") {:basic-auth [riker password]})]
    (-> (json/parse-string body true) :sessionId)))

(login)
