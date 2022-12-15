(ns startrek.test.fixtures
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [donut.system :as ds]
   [next.jdbc :as jdbc]
   [next.jdbc.transaction :as transaction]
   [startrek.test.system :as test-system]))

(set! *warn-on-reflection* true)

(def ^:dynamic *test-system* nil)

(def with-test-system
  (fn [f]
    (let [system (test-system/start)]
      (try
       (binding [*test-system* system]
         (f))
       (finally
        (test-system/stop system))))))

;; https://github.com/seancorfield/next-jdbc/blob/develop/doc/migration-from-clojure-java-jdbc.md#transactions
(def with-rollback
  (fn [f]
    (log/debug "Starting a test transaction that will be rolled back once the test has completed...")
    (let [{::ds/keys [instances]} *test-system*
          {{:keys [db]} :app-config} instances]
      (jdbc/with-transaction [tx db {:rollback-only true}]
        (binding [transaction/*nested-tx* :ignore ;;mimic the behaviour of clojure.java.jdbc, whereby the outer transaction controls the inner transaction. See link above for more info.
                  *test-system* (assoc-in *test-system* [::ds/instances :app-config :db] tx)]
          (f))))))
