(ns startrek.api.starship.specs
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def should-be-a-uuid "should be a type 4 UUID")
(def between-1-and-15 "should be between 1 and 15 characters")
(def between-1-and-20 "should be between 1 and 20 characters")
(def between-1-and-30 "should be between 1 and 30 characters")
(def between-5-and-30 "should be between 5 and 30 characters")
(def between-2000-and-3000 "should be between 2000 and 3000")

(def create [:map
             {:closed true}
             [:captain [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation [:string {:min 1 :max 30 :error/message between-1-and-30}]]
             [:launched [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def modify [:map
             {:closed true}
             [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
             [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:image {:optional true} [:string {:min 5 :max 30 :error/message between-5-and-30}]]])

(def search [:map
             {:closed true}
             [:id {:optional true} [:uuid {:error/message should-be-a-uuid}]]
             [:captain {:optional true} [:string {:min 1 :max 20 :error/message between-1-and-20}]]
             [:affiliation {:optional true} [:string {:min 1 :max 30 :error/message between-5-and-30}]]
             [:launched {:optional true} [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
             [:class {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]
             [:registry {:optional true} [:string {:min 1 :max 15 :error/message between-1-and-15}]]])

(def starship-id [:map
                  {:closed true}
                  [:id uuid?]])
