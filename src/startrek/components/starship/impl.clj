(ns startrek.components.starship.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers :refer [from select where insert-into values on-conflict do-nothing delete-from]]
   [next.jdbc.result-set :as rs]
   [startrek.components.database.interface :as db]))

(set! *warn-on-reflection* true)

(defn ^:private find-by-id-sql
  [id]
  (sql/format {:select [:uuid :created :captain :affiliation :launched :class :registry :image]
               :from [:starship]
               :where [:= :uuid id]}))

(defn find-by-id
  [query {:keys [db] :as app-config}]
  (let [{:keys [id]} query]
    (log/infof "Finding Starship using query '%s'." query)
    (when-let [result (-> (find-by-id-sql id)
                          (db/select db))]
      (log/infof "Found Starship '%s' using query '%s'." (:starship/uuid result) query)
      result)))

(defn ^:private search-sql
  [query]
  (let [{:keys [id captain affiliation launched class registry image]} query]
    (-> (select :uuid :created :captain :affiliation :launched :class :registry :image)
        (from :starship)
        (where
         (when id [:= :uuid id])
         (when captain [:ilike :captain (str captain "%")])
         (when affiliation [:ilike :affiliation (str affiliation "%")])
         (when launched [:= :launched launched])
         (when class [:ilike :class (str class "%")])
         (when registry [:ilike :registry (str registry "%")])
         (when image [:ilike :image (str image "%")]))
        sql/format)))

(defn search
  [query {:keys [db] :as app-config}]
  (log/infof "Finding starships using query '%s'." query)
  (when-let [results (seq (-> (search-sql query)
                              (db/select-many db)))]
    (log/infof "Found '%d' starships(s) using query '%s'." (count results) query)
    results))

(defn ^:private create-sql
  [starship]
  (-> (insert-into :starship)
      (values [starship])
      (on-conflict (do-nothing))
      sql/format))

(defn create
  [starship {:keys [db] :as app-config}]
  (if-let [{:starship/keys [uuid]} (first (search starship app-config))]
    (throw (ex-info "Starship already exists!." {:cause {:id uuid} :status :409 :error :resource.starship.exists}))
    (do
     (log/infof "Saving Starship '%s'." starship)
     (let [{:starship/keys [uuid]} (-> (create-sql starship)
                                       (db/execute db {:return-keys true}))]
       (when uuid
         (log/infof "Starship '%s' saved to PostgreSQL. FTW!" starship)
         uuid)))))

(defn ^:private delete-sql
  [id]
  (-> (delete-from :starship)
      (where [:= :uuid id])
      sql/format))

(defn delete
  [query {:keys [db] :as app-config}]
  (log/infof "Deleting Starship '%s'." query)
  (let [{:keys [id]} query
        {:next.jdbc/keys [update-count]} (-> (delete-sql id)
                                             (db/execute db))]
    (when (and update-count (pos? update-count))
      (log/infof "Deleted Starship '%s'." query)
      id)))

(defn ^:private modify-sql
  [modified-starship]
  (let [{:keys [uuid captain affiliation launched class registry image]} modified-starship]
    (-> (helpers/update :starship)
        (helpers/set {:captain captain
                      :affiliation affiliation
                      :launched launched
                      :class class
                      :registry registry
                      :image image})
        (where [:= :uuid uuid])
        sql/format)))

(defn modify
  [query {:keys [db] :as app-config}]
  (let [{:keys [id]} query]
    (when-let [modified-starship (some-> (find-by-id-sql id)
                                         (db/select db {:builder-fn rs/as-unqualified-lower-maps}) ;; makes it easier to merge if namespaces are removed
                                         (merge query)
                                         (modify-sql)
                                         (db/execute db {:return-keys true}))]
      (log/infof "Modified Starship '%s' to '%s'." id modified-starship)
      modified-starship)))
