(ns catchat-server.handler.site
  (:require
   [compojure.core :as cp]
   [compojure.route :as route]
   [clojure.java.io :as io]
   [integrant.core :as ig]))

(defmethod ig/init-key :catchat-server.handler/site [_ options]
  (cp/routes
   (cp/GET "/" [] (io/resource "public/index.html"))
   (route/resources "/" {:root "public/"})))
