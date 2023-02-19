(ns startrek.core.aux.thymeleaf.interface
  {:author "David Harrigan"}
  (:require
   [startrek.core.aux.thymeleaf.impl :as thymeleaf]))

(set! *warn-on-reflection* true)

(defn render
  ([viewname request]
   (render viewname nil request))
  ([viewname data request]
   (thymeleaf/render viewname data request)))
