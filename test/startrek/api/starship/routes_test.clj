(ns startrek.api.starship.routes-test
  {:author "David Harrigan"}
  (:require
   [clojure.string :refer [blank?]]
   [expectations.clojure.test :as t :refer [defexpect expect expecting in]]
   [startrek.test.generators :as gen]
   [startrek.test.helper :as th]))

(t/use-fixtures :once th/before-all)
(t/use-fixtures :each th/before-each)

(def ^:private starships-url "/api/starships")
(def ^:private ncc-1701-d "67e21206-85bc-415f-9cc3-fc9a981a6248")
(def ^:private ncc-1701-d-request (str starships-url "/" ncc-1701-d))

(defexpect find-all-starships-test
  (expecting
   "Find all starships"
   (let [{:keys [status body] :as response} (-> (th/mock-request :get starships-url)
                                                (th/test-handler))
         {:keys [starships]} body]
     (expect 200 status)
     (expect 7 (count starships)))))

(defexpect delete-starship-test
  (expecting
   "Delete a starship"
   (let [{:keys [status body] :as response} (-> (th/mock-request :delete ncc-1701-d-request)
                                                (th/test-handler))]
     (expect 200 status)
     (expect {:uuid ncc-1701-d} (in body)))))

(defexpect find-starship-test
  (expecting
   "Find a starship"
   (let [{:keys [status body] :as response} (-> (th/mock-request :get ncc-1701-d-request)
                                                (th/test-handler))]
     (expect 200 status)
     (expect {:uuid ncc-1701-d :captain "Jean-Luc Picard"} (in body)))))

(defexpect create-starship-test
  (expecting
   "Create a starship"
   (let [starship (gen/starship)
         {:keys [status body] :as response} (-> (th/json-request starship :post starships-url)
                                                (th/test-handler))
         {:keys [uuid]} body]
     (expect 201 status)
     (expect false (blank? uuid)))))

(defexpect modify-starship-test
  (expecting
   "Create a starship"
   (let [starship (gen/starship)
         {:keys [status body] :as response} (-> (th/json-request starship :post starships-url)
                                                (th/test-handler))
         {:keys [uuid]} body]
     (expect 201 status)
     (expect false (blank? uuid))
     (expecting
      "Find the saved starship"
      (let [{:keys [body] :as response} (-> (th/mock-request :get (str starships-url "/" uuid))
                                            (th/test-handler))]
        (expect {:uuid uuid} (in body))
        (expecting
         "Modify the saved starship"
         (let [mirror-universe-starship (assoc body :captain "Evil Spock")
               {:keys [status body]} (-> (th/json-request mirror-universe-starship :patch (str starships-url "/" uuid))
                                         (th/test-handler))]
           (expect 200 status)
           (expect {:uuid uuid :captain "Evil Spock"} (in body)))))))))
