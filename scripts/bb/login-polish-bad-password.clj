#!/usr/bin/env bb
;;
;;
;;
(require '[org.httpkit.client :as http])

(def riker "riker@starship-enterprise.outer.space")
(def password "BAD-password")

(defn login
  []
  (let [{:keys [body] :as response} @(http/post (str "http://localhost:8080/api/public/officers/login") {:basic-auth [riker password]
                                                                                                         :headers {"accept-language" "pl"}})]
    (println body)))

(login)
