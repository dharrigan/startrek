(ns scripts.build
  (:require
   [org.corfield.build :as bb]))

(def ^:private config {:main 'startrek.main
                       :uber-file "target/app.jar"})
(defn uberjar
  "UberJAR the application.

   This task will create the UberJAR `app.jar` in the `target` directory.
   "
  [opts]
  (-> (merge config opts)
      (bb/clean)
      (bb/uber)))
