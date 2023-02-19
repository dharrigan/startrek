(ns startrek.core.config.impl
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [startrek.core.config.spec :as spec]))

(set! *warn-on-reflection* true)

(defn apply-defaults
  [config]
  (spec/apply-defaults config))

(defn keep-previous-session?
  [{{{:keys [keep-previous-session?]} :core-control} :runtime-config :or {keep-previous-session? false} :as app-config}]
  (when keep-previous-session? (log/warn "Keeping previous session (not removing previous session from cache upon each request)!!"))
  keep-previous-session?)

(defn cookies
  [{{:keys [cookies]} :runtime-config :as app-config}]
  cookies)

(defn cors
  [{{:keys [cors]} :runtime-config :as app-config}]
  cors)

(defn validate
  [config]
  (spec/validate config))
