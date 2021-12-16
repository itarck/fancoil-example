(ns catchat-server.handler.api
  (:require
   [clojure.core.async :refer [go go-loop <! ]]
   [compojure.core :refer [context POST GET]]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [catchat-server.mock.chat-rooms :as m.chat-rooms]))


(defmethod ig/init-key :catchat-server.handler.api/session
  [_ {:keys [random-message-sender]}]
  (let [session-ref (atom {})]
    (fn [request]
      (httpkit/with-channel request channel
        (println "web socket:" (str channel))
        (swap! session-ref assoc channel (java.util.Date.))
                            
        (let [{:keys [message-chan]} random-message-sender]
          (go-loop []
            (let [message (<! message-chan)]
              (doseq [[ch _] @session-ref]
                (httpkit/send! ch (str message))))
            (recur)))

        (httpkit/on-receive channel (fn [data]
                                      (let [new-message (m.chat-rooms/insert-new-message (read-string data))]
                                        (println "receive new message:" (str new-message))
                                        (doseq [[ch _] @session-ref]
                                          (httpkit/send! ch (str new-message))))))
        (httpkit/on-close channel (fn [status]
                                    (swap! session-ref dissoc channel)
                                    (println channel "closed, status" status)))))))


(defmethod ig/init-key :catchat-server.handler/api
  [_ {:keys [session-handler]}]
  (context "/api" []
    (POST "/get-rooms" []
      (str (m.chat-rooms/get-rooms)))
    (POST "/whoami" []
      (str (m.chat-rooms/whoami)))
    (GET "/session" []
      session-handler)))


