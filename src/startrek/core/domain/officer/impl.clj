(ns startrek.core.domain.officer.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers :refer [from select where]]
   [startrek.core.database.interface :as db]))

(set! *warn-on-reflection* true)

(defn ^:private find-by-email-address-sql
  [{:keys [email-address] :as query}]
  (-> (select :*)
      (from :officer)
      (where [:= :email-address email-address])
      sql/format))

(defn find-officer
  [query {:keys [db] :as app-config}]
  (log/infof "Finding officer using query '%s'." query)
  (when-let [{:officer/keys [uuid] :as result} (->> (find-by-email-address-sql query)
                                                    (db/select db))]
    (log/infof "Found officer '%s' using query '%s'." uuid query)
    result))
