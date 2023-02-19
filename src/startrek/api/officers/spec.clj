(ns startrek.api.officers.spec
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private LoginResponse
  [:map
   [:login/session-id :string]])

(def post-login-response LoginResponse)
