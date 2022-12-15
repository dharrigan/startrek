(ns startrek.api.starship.routes-test
  {:author "David Harrigan"}
  (:require
   [expectations.clojure.test :as t :refer [defexpect expect expecting]]
   [startrek.test.helper :as th]))

(t/use-fixtures :once th/before-all)
(t/use-fixtures :each th/before-each)

(def ^:private starships-url "/api/starships")

(defexpect find-all-starships-test
  (expecting
   "Find all starships"
   (let [{:keys [status body] :as response} (-> (th/mock-request :get starships-url)
                                                (th/test-handler))]
     (expect 200 status)
     (expect 7 (count body)))))
