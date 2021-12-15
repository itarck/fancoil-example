(ns catchat-server.handler.api
  (:require
   [compojure.core :refer :all]
   [integrant.core :as ig]
   [catchat-server.model.chat-rooms :as m.chat-rooms]))


(defmethod ig/init-key :catchat-server.handler/api [_ config]
  (context "/api" []
    (GET "/echo" []
      "echo")
    (GET "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))
    (POST "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))))

