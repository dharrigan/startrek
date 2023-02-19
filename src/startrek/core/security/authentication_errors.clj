(ns startrek.core.security.authentication-errors
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def missing-or-invalid-basic-credentials {:message "Missing or invalid Basic credentials" :data {:error :missing.or.invalid.basic.credentials}})
(def missing-or-invalid-token-credentials {:message "Missing or invalid Token credentials" :data {:error :missing.or.invalid.token.credentials}})
(def missing-or-invalid-cookie-credentials {:message "Missing or invalid Cookie credentials" :data {:error :missing.or.invalid.cookie.credentials}})
