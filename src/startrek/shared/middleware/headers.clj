(ns startrek.shared.middleware.headers
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def ^:private cache-control "Cache-Control")
(def ^:private expires "Expires")
(def ^:private pragma "Pragma")
(def ^:private x-content-type-options "X-Content-Type-Options")
(def ^:private x-frame-options "X-Frame-Options")
(def ^:private x-xss-protection "X-XSS-Protection")

(def ^:private security-headers
  {cache-control "no-cache, no-store, max-age=0, must-revalidate"
   expires "0"
   pragma "no-cache"
   x-content-type-options "nosniff"
   x-frame-options "DENY"
   x-xss-protection "1; mode=block"})

(defn ^:private enrich-response
  [{:keys [headers] :as response}]
  (assoc response :headers (merge security-headers headers)))

(defn ^:private with-headers
  [handler]
  (fn [request]
    (-> (handler request)
        (enrich-response))))

(def headers-middleware
  {:name ::headers
   :description "Adds security headers to each request/response."
   :wrap with-headers})
