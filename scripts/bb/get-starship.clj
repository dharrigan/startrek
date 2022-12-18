#!/usr/bin/env bb
;;
;;
;;
(require '[cheshire.core :as json])
(require '[clojure.tools.cli :refer [parse-opts]])
(require '[org.httpkit.client :as http])

(defn get-request
  [url-path]
  (let [{:keys [body]} @(http/get (str "http://localhost:8080" url-path))]
    (json/parse-string body true)))

(defn get-starship
  [uuid]
  (get-request (str "/api/starships/" uuid)))

(def cli-options
  [["-u" "--uuid UUID" "Starship UUID"]])

(let [{{:keys [uuid]} :options :as cli} (parse-opts *command-line-args* cli-options)]
  (if uuid
    (get-starship uuid)
    (println "Please supply a starship uuid, i.e., -u 5d1158e5-d361-4d89-9593-040d65487725")))
