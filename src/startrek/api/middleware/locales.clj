(ns startrek.api.middleware.locales
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log])
  (:import
   [java.util Locale Locale$LanguageRange]))

(set! *warn-on-reflection* true)

;; PRIVATE FUNCTIONS AND DEFINITIONS ↓

(defn ^:private parse-accept-language
  [header]
  (mapv (fn [language-range]
          (-> ^String (.getRange ^Locale$LanguageRange language-range)
              ^Locale (Locale/forLanguageTag)
              (str)
              (keyword))) (Locale$LanguageRange/parse header)))

(defn ^:private with-locales
  [handler]
  (fn [request]
    (let [header (get-in request [:headers "accept-language"] "en-GB")
          locales (parse-accept-language header)]
      (log/debugf "Parsed accept-language '%s' to '%s'" header locales)
      (handler (assoc request :locales locales)))))

;; PUBLIC FUNCTIONS AND DEFINITIONS ↓

(def locales-middleware
  {:name ::locale
   :description "Parses the accept-language header into locales and put it into the request map as :locales."
   :wrap with-locales})
