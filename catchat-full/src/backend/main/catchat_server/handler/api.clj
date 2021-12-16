(ns catchat-server.handler.api
  (:require
   [compojure.core :refer [context POST GET]]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [catchat-server.model.chat-rooms :as m.chat-rooms]))


(defmethod ig/init-key :catchat-server.handler/api [_ _config]
  (context "/api" []
    (POST "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))
    (POST "/whoami" []
      (str (m.chat-rooms/whoami)))

    (GET "/session" []
      (fn [request]
        (httpkit/with-channel request channel
          (println "web socket:" (str channel))
          (httpkit/on-receive channel (fn [data]
                                        (println "receive data:" (str data))
                                        (httpkit/send! channel (str "sending: " data))))
          (httpkit/on-close channel (fn [status]
                                      (println channel "closed, status" status))))))))

