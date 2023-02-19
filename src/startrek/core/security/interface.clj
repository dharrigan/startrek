(ns startrek.core.security.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.security.impl :as security]))

(set! *warn-on-reflection* true)

(defn basic-authentication
  [credentials app-config]
  (security/basic-authentication credentials app-config))

(defn cookie-authentication
  [credentials app-config]
  (security/cookie-authentication credentials app-config))

(defn token-authentication
  [credentials app-config]
  (security/token-authentication credentials app-config))
