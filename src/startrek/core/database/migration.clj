(ns startrek.core.database.migration
  {:author "David Harrigan"}
  (:import
   [org.flywaydb.core Flyway]
   [org.flywaydb.core.api.configuration FluentConfiguration]))

(set! *warn-on-reflection* true)

;; When you work with arrays in Clojure (and wish to avail of performance
;; improvements by using type hinting) you have to make sure that the type of
;; the array is known to the compiler. The type hints for primitive arrays are
;; ^longs, ^chars, ^doubles, etc.; for arrays of objects you have to use an
;; unwieldy construct like ^"[Ljava.lang.String;"

(defn ^:private flyway
  ^Flyway
  [datasource migration-locations]
  (Flyway. (doto ^FluentConfiguration
            (FluentConfiguration.)
             (.dataSource datasource)
             (.locations ^"[Ljava.lang.String;" (into-array String migration-locations)))))

(defn migrate
  "Migrate a database from one version to the next version using flyway.
   Takes a `datasource` and a list of `migration-locations` which are fed to
   flyway to perform the migration.
   "
  [datasource migration-locations]
  (.migrate (flyway datasource migration-locations)))
