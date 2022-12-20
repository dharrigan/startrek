#!/usr/bin/env bb
;;
;;
;;
(require '[cheshire.core :as json])
(require '[org.httpkit.client :as http])

(def riker "riker@starship-enterprise.outer.space")
(def password "password")
(def content-type "application/json;charset=utf-8")

(defn login
  []
  (let [{:keys [body] :as response} @(http/post (str "http://localhost:8080/api/public/officers/login") {:basic-auth [riker password]})]
    (-> (json/parse-string body true) :sessionId)))

(def defiant
  (json/encode {:captain "Worf, son of Mogh"
                :affiliation "United Federation of Planets"
                :launched 2370
                :class "Defiant"
                :registry "NX-74205"}))

(defn create-request
  [session-id url-path]
  (let [request {:headers {"Authorization" (str "Token " session-id) "Content-Type" content-type} :body defiant}
        {:keys [body]} @(http/post (str "http://localhost:18080" url-path) request)]
    (json/parse-string body true)))

(defn create-starship
  []
  (let [session-id (login)]
    (create-request session-id (str "/api/private/starships"))))

(create-starship)
