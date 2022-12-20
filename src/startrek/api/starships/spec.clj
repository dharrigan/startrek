(ns startrek.api.starships.spec
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def between-1-and-15 "should be between 1 and 15 characters")
(def between-1-and-20 "should be between 1 and 20 characters")
(def between-1-and-30 "should be between 1 and 30 characters")
(def between-2000-and-3000 "should be between 2000 and 3000")
(def between-5-and-30 "should be between 5 and 30 characters")
(def should-be-a-uuid "should be a type 4 UUID")

(def create [:map
             [:captain [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation [:string {:min 1 :max 30 :error/message between-1-and-30}]]
             [:launched [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def modify [:map
             [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
             [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def search [:map
             [:id {:optional true} [:uuid {:error/message should-be-a-uuid}]]
             [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
             [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]])

(def starship-id [:map [:id :uuid]])

(def ^:private starship-response
  [:map
   [:starship/uuid :uuid]
   [:starship/captain :string]
   [:starship/affiliation :string]
   [:starship/launched pos-int?]
   [:starship/class :string]
   [:starship/registry :string]
   [:starship/image {:optional true} [:maybe [:string]]]])

(def create-starship-response [:map [:starship/uuid :uuid]])
(def delete-starship-response [:map [:starship/uuid :uuid]])
(def get-starship-response starship-response)
(def patch-starship-response starship-response)
(def search-starships-response [:map [:starships [:maybe [:vector starship-response]]]])
