(ns startrek.api.starships.spec
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private between-1-and-15 "should be between 1 and 15 characters")
(def ^:private between-1-and-20 "should be between 1 and 20 characters")
(def ^:private between-1-and-30 "should be between 1 and 30 characters")
(def ^:private between-2000-and-3000 "should be between 2000 and 3000")
(def ^:private between-5-and-30 "should be between 5 and 30 characters")

(def ^:private CreateStarshipRequest
  [:map
   [:captain [:string {:min 1 :max 20 :error/message between-1-and-20}]]
   [:affiliation [:string {:min 1 :max 30 :error/message between-1-and-30}]]
   [:launched [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
   [:class [:string {:min 1 :max 15 :error/message between-1-and-15}]]
   [:registry [:string {:min 1 :max 15 :error/message between-1-and-15}]]
   [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def ^:private PatchStarshipRequest
  [:map
   [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
   [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
   [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
   [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
   [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
   [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def ^:private SearchStarshipsRequest
  [:map
   [:id {:optional true} [:uuid {:error/message :coercion/uuid}]]
   [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
   [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
   [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
   [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
   [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]])

(def ^:private StarshipId [:map [:id [:uuid {:error/message :coercion/uuid}]]])

(def ^:private StarshipResponse
  [:map
   [:starship/uuid :uuid]
   [:starship/created inst?]
   [:starship/captain :string]
   [:starship/affiliation :string]
   [:starship/launched pos-int?]
   [:starship/class :string]
   [:starship/registry :string]
   [:starship/image {:optional true} [:maybe [:string]]]])

(def starship-id StarshipId)
(def get-starship-request StarshipId)
(def create-starship-request CreateStarshipRequest)
(def create-starship-response [:map [:starship/uuid :uuid]])
(def delete-starship-response [:map [:starship/uuid :uuid]])
(def get-starship-response StarshipResponse)
(def patch-starship-request PatchStarshipRequest)
(def patch-starship-response StarshipResponse)
(def search-starships-request SearchStarshipsRequest)
(def search-starships-response [:map [:starships [:maybe [:vector StarshipResponse]]]])
