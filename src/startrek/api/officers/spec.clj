(ns startrek.api.officers.spec
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private login-response
  [:map
   [:login/session-id :string]])

(def post-login-response login-response)
