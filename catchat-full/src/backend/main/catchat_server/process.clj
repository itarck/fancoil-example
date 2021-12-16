(ns catchat-server.process
  (:require
   [clojure.core.async :refer [go go-loop >! <! chan] :as async]
   [org.httpkit.server :as httpkit]
   [integrant.core :as ig]
   [catchat-server.mock.chat-rooms :as mock]))


(defn- rand-n [min max]
  (+ min (rand-int (- max min))))


(defmethod ig/init-key ::random-message-sender
  [_ {:keys [session-ref]}]
  (go-loop []
    (<! (async/timeout (rand-n 500 1500)))
    (let [new-message (mock/generate-new-message)]
      (doseq [[ch _] @session-ref]
        (httpkit/send! ch (str new-message))))
    (recur)))

