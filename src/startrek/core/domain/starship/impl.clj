(ns startrek.core.domain.starship.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [honey.sql :as sql]
   [honey.sql.helpers :as helpers :refer [from select where insert-into values on-conflict do-nothing delete-from returning]]
   [next.jdbc.result-set :as rs]
   [startrek.core.database.interface :as db]
   [startrek.core.errors.interface :as errors]))

(set! *warn-on-reflection* true)

(defn ^:private find-by-id-sql
  [id]
  (-> {:select [:uuid :created :captain :affiliation :launched :class :registry :image]
       :from [:starship]
       :where [:= :uuid id]}
      sql/format))

(defn find-by-id
  [query {:keys [db] :as app-config}]
  (let [{:keys [id]} query]
    (log/infof "Finding starship using query '%s'." query)
    (when-let [result (->> (find-by-id-sql id)
                           (db/select db))]
      (log/infof "Found starship '%s' using query '%s'." (:starship/uuid result) query)
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
  (log/infof "Finding starships using query '%s'." (or query {}))
  (when-let [results (->> (search-sql query)
                          (db/select-many db))]
    (log/infof "Found '%d' starships(s) using query '%s'." (count results) (or query {}))
    results))

(defn ^:private create-sql
  [starship]
  (-> (insert-into :starship)
      (values [starship])
      (on-conflict (do-nothing))
      (returning :*)
      sql/format))

(defn ^:private starship-with-id-exists
  [uuid]
  {:message (format "Starship '%s' already exists!" uuid) :data {:error :resource.exists :opts {:resource "starship" :id uuid}}})

(defn create
  [starship {:keys [db] :as app-config}]
  (if-let [{:starship/keys [uuid]} (first (search starship app-config))]
    (errors/throw-resource-exists-exception (starship-with-id-exists uuid))
    (do
      (log/infof "Saving starship '%s'." starship)
      (let [{:starship/keys [uuid]} (->> (create-sql starship)
                                         (db/execute db))]
        (when uuid
          (log/infof "Starship '%s' saved to PostgreSQL. FTW!" starship)
          uuid)))))

(defn ^:private delete-sql
  [id]
  (-> (delete-from :starship)
      (where [:= :uuid id])
      (returning :*)
      sql/format))

(defn delete
  [query {:keys [db] :as app-config}]
  (log/infof "Deleting starship '%s'." query)
  (let [{:keys [id]} query
        result (->> (delete-sql id)
                    (db/execute db))]
    (when result
      (log/infof "Deleted starship '%s'." query)
      result)))

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
        (returning :*)
        sql/format)))

(defn modify
  [query {:keys [db] :as app-config}]
  (let [{:keys [id]} query]
    (when-let [saved-starship (-> (db/select db (find-by-id-sql id) {:builder-fn rs/as-unqualified-lower-maps}))]
      (let [updated-starship (merge saved-starship query)
            updated-starship-sql (modify-sql updated-starship)
            modified-starship' (db/execute db updated-starship-sql)]
        (log/infof "Modified starship '%s' to '%s'." id modified-starship')
        modified-starship'))))
