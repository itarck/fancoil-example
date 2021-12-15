(ns catchat-server.handler.api
  (:require
   [compojure.core :as cp]
   [integrant.core :as ig]
   [catchat-server.model.chat-rooms :as m.chat-rooms]))


(defmethod ig/init-key :catchat-server.handler/api [_ _config]
  (cp/context "/api" []
    (cp/POST "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))))

