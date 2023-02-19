(ns startrek.core.utils.i18n
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [taoensso.tempura :refer [tr]])
  (:import
   [java.text MessageFormat]))

(set! *warn-on-reflection* true)

(def ^:private dictionary
  {:en
   {:coercion {:__load-resource "i18n/coercion/en.edn"}
    :api {:__load-resource "i18n/api/en.edn"}
    :ui {:lcars {:__load-resource "i18n/ui/lcars/en.edn"}}
    :validation {:__load-resource "i18n/validation/en.edn"}}})

(defn ^:private missing-resource
  [{:keys [_ locales resource-ids _] :as opts}]
  (log/infof "Missing i18n resource '%s' from locales '%s'." resource-ids locales)
  (format "key %s from locales %s missing" resource-ids locales))

(def ^:private i18n (partial tr {:dict dictionary :missing-resource-fn missing-resource}))

(defn resolve-message
  [locales resource-id & [parameters]]
  (let [resolved-message (i18n locales [resource-id])]
    (if (> (count parameters) 0) (MessageFormat/format resolved-message parameters) resolved-message)))
