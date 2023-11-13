(ns startrek.core.aux.thymeleaf.impl
  {:author "David Harrigan"}
  (:require
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as csk-extras]
   [startrek.core.utils.i18n :refer [resolve-message]]
   [startrek.shared.utils.platform :as platform-utils])
  (:import
   [java.util Locale]
   [org.thymeleaf TemplateEngine]
   [org.thymeleaf.context Context IContext]
   [org.thymeleaf.messageresolver IMessageResolver]
   [org.thymeleaf.templateresolver ClassLoaderTemplateResolver]))

(set! *warn-on-reflection* true)

(def ^:private template-resolver-defaults {:prefix "public/" :suffix ".html" :cacheable? false :cache-ttl-ms 0})

(def ^:private lcars-template-aliases
  {"lcarsBase" "lcars/base"
   "lcarsFooter" "lcars/common/components/footer"
   "lcarsHeader" "lcars/common/components/header"
   "lcarsNavbar" "lcars/common/components/navbar"})

(def ^:private starships-template-aliases
  {"starshipsDetails" "lcars/starships/details/components/details"
   "starshipsTable" "lcars/starships/dashboard/components/table"})

(defn ^:private classpath-template-resolver
  [config]
  (let [{:keys [prefix suffix cacheable? cache-ttl-ms]} (merge template-resolver-defaults config)]
    (doto (ClassLoaderTemplateResolver.)
      ;;
      ;; Template aliases
      ;;
      (.setTemplateAliases (merge lcars-template-aliases starships-template-aliases))
      ;;
      ;; Other settings
      ;;
      (.setCacheable cacheable?)
      (.setCacheTTLMs cache-ttl-ms)
      (.setPrefix prefix)
      (.setSuffix suffix))))

(def ^:private tempura-message-resolver
  (reify IMessageResolver
    (getName [_] "Tempura Message Resolver")
    (getOrder [_] (int 0))
    (resolveMessage [_ context _ key message-parameters]
      (resolve-message (.getVariable context "locales") (keyword key) message-parameters))
    (createAbsentMessageRepresentation [_ _ _ _ _]
      (comment "This will never fire as the `resolve-message` above *always* returns something, even indicating a missing key"))))

(defn render
  [viewname data {{:keys [template-engine]} :app-config :keys [locales] :as request}]
  (let [context (Context. (Locale. (name (first locales))))] ;; use the first locale as the default for rendering i18n text
    (when-let [data' (csk-extras/transform-keys csk/->camelCaseString data)]
      (.setVariables context data'))
    (.setVariable context "locales" locales)
    (.setVariable context "version" (platform-utils/version))
    (.process ^TemplateEngine template-engine ^String viewname ^IContext context)))

;; DONUT LIFECYCLE FUNCTIONS ↓

(defn start
  ^ClassLoaderTemplateResolver
  [config]
  (doto (TemplateEngine.)
    (.setTemplateResolver (classpath-template-resolver config))
    (.addMessageResolver tempura-message-resolver)))
