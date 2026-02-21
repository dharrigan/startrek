#!/usr/bin/env bb
;;
;;
;;
(require '[cheshire.core :as json])
(require '[clojure.tools.cli :refer [parse-opts]])
(require '[org.httpkit.client :as http])

(def riker "riker@starship-enterprise.outer.space")
(def password "password")

(defn login
  []
  (let [{:keys [body] :as response} @(http/post "http://localhost:8080/api/public/officers/login" {:basic-auth [riker password]})]
    (-> (json/parse-string body true) :sessionId)))

(defn delete-request
  [session-id url-path]
  (let [headers {:headers {"Authorization" (str "Token " session-id)}}
        {:keys [body]} @(http/delete (str "http://localhost:8080" url-path) headers)]
    (json/parse-string body true)))

(defn get-starship
  [uuid]
  (let [session-id (login)]
    (delete-request session-id (str "/api/private/starships/" uuid))))

(def cli-options
  [["-u" "--uuid UUID" "Starship UUID"]])

(let [{{:keys [uuid]} :options :as cli} (parse-opts *command-line-args* cli-options)]
  (if uuid
    (get-starship uuid)
    (println "Please supply a starship uuid, i.e., -u 5d1158e5-d361-4d89-9593-040d65487725")))
