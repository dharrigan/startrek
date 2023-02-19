(ns startrek.ui.lcars.starships.spec
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private GetStarshipRequest
  [:map [:id [:uuid {:error/message :coercion/uuid}]]])

(def get-starship-request GetStarshipRequest)
