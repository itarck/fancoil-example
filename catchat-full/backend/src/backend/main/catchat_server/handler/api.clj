(ns catchat-server.handler.api
  (:require
   [compojure.core :refer [context POST GET routes]]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [ring.util.response :refer [response]]
   [ring.middleware.format-params :refer [wrap-restful-params]]
   [ring.middleware.format-response :refer [wrap-restful-response]]
   [catchat-server.mock-db :as mock-db]))

(defmethod ig/init-key :catchat-server.handler.api/session-ref
  [_ _]
  (atom {}))

(defmethod ig/init-key :catchat-server.handler.api/session-handler
  [_ {:keys [session-ref]}]
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
                                  (println channel "closed, status" status))))))


(defmethod ig/init-key :catchat-server.handler/api
  [_ {:keys [session-handler]}]
  (routes
   (-> (context "/api" []
         (POST "/get-rooms" []
           (response (mock-db/get-rooms)))
         (POST "/whoami" []
           (response (mock-db/whoami)))
         (POST "/get-user" [id]
           (response (mock-db/get-user id))))
       (wrap-restful-params)
       (wrap-restful-response))
   (GET "/api/session" []
     session-handler)))


