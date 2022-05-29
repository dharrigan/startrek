(ns startrek.main
  {:author "David Harrigan"}
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [donut.system :as ds]
   [startrek.system]) ;; required in order to load in the defmulti's that define the donut `named-system`'s
  (:gen-class))

(set! *warn-on-reflection* true)

(def ^:private cli-options
  [["-e" "--environment ENVIRONMENT" "Environment to use, i.e., local"
    :default :local
    :parse-fn #(keyword %)]])

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var :unused-binding]}
(defn ^:private process
  [arguments running-system]
  ;; process arguments here...
  (ds/signal running-system :stop)
  (shutdown-agents))

(defn -main
  [& args]
  (let [{{:keys [environment]} :options :keys [arguments]} (parse-opts args cli-options)
        running-system (ds/start environment)] ;; fire up the `named-system`, e.g., :local
    (.addShutdownHook
     (Runtime/getRuntime)
     (new Thread #(ds/signal running-system ::ds/stop)))
    (if (seq arguments)
      (process arguments running-system) ;; application run from the command line with arguments.
      @(promise)))) ;; application run from the command line, no arguments, keep webserver running.
