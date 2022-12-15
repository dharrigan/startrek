(ns scripts.build
  (:require
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb]))

(def ^:private config {:main 'startrek.main
                       :uber-file "target/app.jar"})
(defn uberjar
  "UberJAR the application.

   This task will create the UberJAR `app.jar` in the `target` directory.
   "
  [opts]
  (spit "resources/version.txt" (b/git-process {:git-args "rev-parse --short=8 HEAD"}))
  (-> {:compile-opts {:direct-linking true}}
      (merge config opts)
      (bb/clean)
      (bb/uber)))
