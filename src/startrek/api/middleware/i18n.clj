(ns startrek.api.middleware.i18n
  {:author "David Harrigan"})

(set! *warn-on-reflection* true)

(def dictionary
  {:en
   {:general.exception "STAR00010::Sorry, we were unable to process your request!"

    :resource.starship.exists "STAR20010::Starship already exists! Starship Id '%s'."

    :http.internal-server-error "STAR80500::Sorry, HTTP internal server error due to '%s'!"

    :service.unavailable "STAR90010::Sorry, the service is currently unavailable!"
    :database.unavailable "STAR90020::Sorry, the database is currently unavailable!"}})
