(ns startrek.api.util.http
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def application-json "application/json")

(def ok 200)
(def created 201)
(def not-found 404)

(def internal-server-error 500)
