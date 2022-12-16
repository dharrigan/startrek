(ns startrek.core.domain.starship.model
  {:author "David Harrigan"}
  (:require
   [clojure.test.check.generators :as gen]
   [malli.generator :as mg]))

(def between-1-and-15 "should be between 1 and 15 characters")
(def between-1-and-20 "should be between 1 and 20 characters")
(def between-1-and-30 "should be between 1 and 30 characters")
(def between-2000-and-3000 "should be between 2000 and 3000")
(def between-5-and-30 "should be between 5 and 30 characters")

(defn ^:private sensible-string
  [min max]
  (gen/fmap #(apply str %) (gen/vector gen/char-alpha min max)))

(def ^:private Starship
  [:map
   [:captain [:string {:gen/gen (sensible-string 1 20) :min 1 :max 20 :error/message between-1-and-20}]]
   [:affiliation [:string {:gen/gen (sensible-string 1 30) :min 1 :max 30 :error/message between-1-and-30}]]
   [:launched [:int {:min 2000 :max 3000 :error/message between-2000-and-3000}]]
   [:class [:string {:gen/gen (sensible-string 1 15) :min 1 :max 15 :error/message between-1-and-15}]]
   [:registry [:string {:gen/gen (sensible-string 1 15) :min 1 :max 15 :error/message between-1-and-15}]]
   [:image [:string {:gen/gen (sensible-string 5 30) :min 5 :max 30 :error/message between-5-and-30}]]])

(defn gen-starship
  [overrides]
  (merge (mg/generate Starship) overrides))
