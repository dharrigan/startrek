(ns startrek.core.session.repository
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [jsonista.core :as json]
   [startrek.core.cache.interface :as cache]
   [startrek.shared.constants :refer [lcars-session]]))

(set! *warn-on-reflection* true)

(def ^:private keyword-mapper (json/object-mapper {:strip-nils true :decode-key-fn true}))

(defn ^:private lookup-session-key-wildcard
  [{:keys [prefix session-id] :or {prefix lcars-session}} app-config]
  (some-> (str prefix session-id ":*")
          (cache/redis-keys app-config)
          (first))) ;; there should be only ever one unique session belonging the principal!!

(defn delete-session
  [{:keys [session-id] :as opts} app-config]
  (when (some-> (lookup-session-key-wildcard opts app-config)
                (cache/redis-del app-config))
    (log/tracef "Deleted session '%s' from the sessions cache." session-id)))

(defn get-session
  [{:keys [session-id] :as opts} app-config]
  (when-let [session (some-> (lookup-session-key-wildcard opts app-config)
                             (cache/redis-get app-config)
                             (json/read-value keyword-mapper))]
    (log/tracef "Found session '%s' in the sessions cache." session-id)
    session))

(defn save-session
  [{:keys [prefix session-id session] :or {prefix lcars-session} :as opts} app-config]
  (let [session-id' (str prefix session-id)
        session' (cache/redis-put session-id' session opts app-config)]
    (log/tracef "Saved session '%s' in the sessions cache." session-id')
    session'))
