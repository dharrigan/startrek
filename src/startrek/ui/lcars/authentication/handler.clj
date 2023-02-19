(ns startrek.ui.lcars.authentication.handler
  {:author "David Harrigan"}
  (:require
   [ring.util.response :as response]
   [startrek.api.officers.client :as officers-client]
   [startrek.core.aux.thymeleaf.interface :as thymeleaf]
   [startrek.core.cache.interface :as cache]
   [startrek.shared.constants :refer [authorization lcars-token]]
   [startrek.ui.utils.cookie :as cookie-utils]
   [startrek.ui.utils.htmx :as htmx-utils]
   [startrek.ui.utils.response :as response-utils]))

(set! *warn-on-reflection* true)

(defn login-page
  [{:keys [anti-forgery-token] :as request}]
  (-> (thymeleaf/render "lcars/login/index" {:csrf anti-forgery-token} request)
      (response-utils/html)))

(defn post-login-success
  [{{:keys [session-id]} :identity :keys [app-config] :as request}]
  (when-let [token (cookie-utils/get-lcars-token-cookie request)]
    (cache/redis-del (str lcars-token token) app-config)
    (-> (response/response nil)
        (cookie-utils/set-lcars-session-cookie session-id app-config)
        (cookie-utils/unset-lcars-token-cookie app-config)
        (htmx-utils/hx-redirect "starships"))))

(defn logout
  [{:keys [app-config] :as request}]
  (when-let [session-id (cookie-utils/get-lcars-session-cookie request)]
    (-> (assoc-in request [:headers authorization] (str "Token " session-id))
        (officers-client/logout)
        (cookie-utils/unset-lcars-session-cookie app-config)
        (htmx-utils/hx-redirect "/"))))
