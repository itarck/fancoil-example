(ns catchat-server.handler.api
  (:require
   [clojure.core.async :refer [go go-loop <! ]]
   [compojure.core :refer [context POST GET]]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [catchat-server.model.chat-rooms :as m.chat-rooms]))


(defmethod ig/init-key :catchat-server.handler.api/session
  [_ {:keys [random-message-sender]}]
  (fn [request]
    (httpkit/with-channel request channel
      (println "web socket:" (str channel))
                          
      (let [{:keys [message-chan]} random-message-sender]
        (go-loop []
          (let [message (<! message-chan)]
            (httpkit/send! channel (str message)))
          (recur)))
                          
      (httpkit/on-receive channel (fn [data]
                                    (println "receive data:" (str data))
                                    (httpkit/send! channel (str data))))
      (httpkit/on-close channel (fn [status]
                                  (println channel "closed, status" status))))))


(defmethod ig/init-key :catchat-server.handler/api
  [_ {:keys [session-handler]}]
  (context "/api" []
    (POST "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))
    (POST "/whoami" []
      (str (m.chat-rooms/whoami)))
    (GET "/session" []
      session-handler)))



