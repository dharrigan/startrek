(ns startrek.api.middleware.i18n
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def dictionary
  {:en
   {;;
    :database.unavailable "STAR90020::Sorry, the database is currently unavailable!"
    :general.exception "STAR00010::Sorry, we were unable to process your request!"
    :http.internal-server-error "STAR10500::Sorry, HTTP internal server error due to '%s'!"
    :missing.or.invalid.basic.credentials "STAR30010::Sorry, missing or invalid credentials"
    :missing.or.invalid.token.credentials "STAR30020::Sorry, missing or invalid credentials"
    :resource.does.not.exist "STAR20010::Sorry, resource '%s' with id '%s' does not exist!"
    :resource.exists "STAR20020::Sorry, resource '%s' with id '%s' already exists!"
    :service.unavailable "STAR90010::Sorry, the service is currently unavailable!"}})
