(ns startrek.core.utils.i18n
  {:author "David Harrigan"}
  (:require
   [clojure.tools.logging :as log]
   [taoensso.tempura :refer [tr]]))

(set! *warn-on-reflection* true)

(def ^:private dictionary
  {:en
   {:api {:__load-resource "i18n/api/en.edn"}
    :coercion {:__load-resource "i18n/coercion/en.edn"}}
   :pl
   {:api {:__load-resource "i18n/api/pl.edn"}}})

(defn ^:private missing-resource
  [{:keys [_ locales resource-ids _]}]
  (log/infof "Missing i18n resource '%s' from locales '%s'." resource-ids locales)
  (format "key %s from locales %s missing" resource-ids locales))

(def ^:private i18n (partial tr {:dict dictionary :missing-resource-fn missing-resource}))

(defn resolve-message
  [locales resource-id]
  (i18n locales [resource-id]))
