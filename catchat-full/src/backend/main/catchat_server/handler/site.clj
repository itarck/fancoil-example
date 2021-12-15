(ns catchat-server.handler.site
  (:require
   [compojure.core :refer :all]
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(defmethod ig/init-key :catchat-server.handler/site [_ options]
  (GET "/" []
    (io/resource "catchat_server/handler/site/index.html")))
