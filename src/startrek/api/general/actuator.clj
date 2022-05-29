(ns startrek.api.general.actuator
  {:author "David Harrigan"}
  (:require
   [startrek.api.general.metrics :as metrics]
   [startrek.api.util.http :as http-util]
   [startrek.components.database.interface :as db]))

(set! *warn-on-reflection* true)

(defn ^:private database-health-check
  [app-config]
  (if (db/health-check app-config)
    {:db [{:status "UP"}] :starships {:status "There's Klingons on the starboard bow, starboard bow Jim!"}}
    {:db [{:status "DOWN"}]}))

(defn ^:private health-check
  [{:keys [app-config] :as request}]
  {:status http-util/ok :body {:components (database-health-check app-config)}})

(def routes
  ["/actuator"
   ["/health" {:get {:handler health-check}}]
   ["/prometheus" {:get {:handler metrics/metrics}}]])
