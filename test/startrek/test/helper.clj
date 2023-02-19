(ns startrek.test.helper
  {:author "David Harrigan"}
  (:require
   [cheshire.core :as json]
   [clojure.java.io :as io]
   [clojure.test :as test]
   [donut.system :as ds]
   [ring.mock.request :as mock]
   [startrek.core.security.interface :as security]
   [startrek.router :as router]
   [startrek.shared.constants :as constants]
   [startrek.test.fixtures :as fixtures]))

(set! *warn-on-reflection* true)

(defn ^:private json?
  [response]
  (= "application/json; charset=utf-8" (get-in response [:headers "Content-Type"])))

(defn ^:private parse-json
  [body]
  (cond
    (string? body) (json/parse-string body keyword)
    (some? body) (json/parse-stream (io/reader body) keyword)))

(def ^:private static-ring-handler #'router/static-ring-handler)

(def before-all (test/join-fixtures [fixtures/with-test-system]))
(def before-each (test/join-fixtures [fixtures/with-rollback]))

(defn app-config
  []
  (get-in fixtures/*test-system* [::ds/instances :app-config]))

(defn mock-request
  [method url & [params]]
  (-> (mock/request method url params)
      (assoc :app-config (app-config))))

(defn test-handler
  [request]
  (let [handler (static-ring-handler (app-config))
        response (handler request)]
    (cond-> response
      (json? response) (update :body parse-json))))

(defn json-request
  [body method url]
  (let [json-body (json/generate-string body {:key-fn name})]
    (-> (mock/request method url json-body)
        (assoc :app-config (app-config))
        (mock/content-type constants/application-json))))

(defn authenticate-as
  [{:keys [app-config] :as request} email-address password]
  (let [credentials {:username email-address :password password}
        {:keys [session-id] :as session} (security/basic-authentication credentials app-config)]
    (assoc-in request [:headers "Authorization"] (str "Token " session-id))))
