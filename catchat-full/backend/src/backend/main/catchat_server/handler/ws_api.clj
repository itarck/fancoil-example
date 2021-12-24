(ns catchat-server.handler.ws-api
  (:require
   [compojure.core :refer [GET]]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [catchat-server.mock-db :as mock-db]))

(defmethod ig/init-key :catchat-server.handler.ws-api/session-ref
  [_ _]
  (atom {}))

(defmethod ig/init-key :catchat-server.handler/ws-api
  [_ {:keys [session-ref]}]
  (GET "/api/session" []
    (fn [request]
      (httpkit/with-channel request channel
        (println "web socket:" (str channel))
        (swap! session-ref assoc channel (java.util.Date.))
        (httpkit/on-receive channel (fn [data]
                                      (let [new-message (mock-db/insert-new-message (read-string data))]
                                        (println "receive new message:" (str new-message))
                                        (doseq [[ch _] @session-ref]
                                          (httpkit/send! ch (str new-message))))))
        (httpkit/on-close channel (fn [status]
                                    (swap! session-ref dissoc channel)
                                    (println channel "closed, status" status)))))))

