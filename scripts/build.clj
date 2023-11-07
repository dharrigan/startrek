(ns scripts.build
  (:require
   [clojure.tools.build.api :as b]))

;;
;; If you're copy&pasting this file from somewhere else, then probably
;; this is the only line you need to change, to reflect the main namespace to
;; compile from (the one with (:gen-class) in it!)
;;
(def ^:private main 'startrek.main)

;;
;; Should be no need to change anything below...
;;
(def ^:private class-dir "target/classes")
(def ^:private uber-file "target/app.jar")
(def ^:private target "target")

(defn ^:private uber-opts
  [opts]
  (assoc opts
         :compile-opts {:direct-linking true}
         :main main
         :uber-file uber-file
         :basis (b/create-basis)
         :class-dir class-dir
         :target target
         :src-dirs ["src"]
         :ns-compile [main]))

(defn uberjar
  "This task will create the UberJAR in the `target` directory."
  [opts]
  (spit "resources/version.txt" (b/git-process {:git-args "rev-parse --short=7 HEAD"}))
  (let [opts' (uber-opts opts)]
    (println (format "Cleaning '%s'..." target))
    (b/delete {:path "target"})
    (println (format "Copying source files to '%s'..." class-dir))
    (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
    (println (format "Compiling '%s' namespace..." (name main)))
    (b/compile-clj opts')
    (println (format "Building UberJAR to '%s'..." uber-file))
    (b/uber opts')
    (println "Finished."))
  opts)
