(ns catchat.module.chat-session
  (:require
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go go-loop >! <!] :as a]
   [haslett.client :as ws]
   [integrant.core :as ig]
   [fancoil.base :as base]))


(defmethod ig/init-key :catchat.module/chat-session
  [_ {:keys [socket receive-method dispatch]}]
  (let [stream {:source (a/chan 10)
                :sink   (a/chan 10)}]
    (ws/connect socket stream)
    (go-loop []
      (let [value (<! (:source stream))]
        (dispatch receive-method (read-string value)))
      (recur))
    stream))

(defmethod base/do! :chat-session/send!
  [{:keys [chat-session]} _ event]
  (go (>! (:sink chat-session) (str event))))


(comment

  (def stream {:source (a/chan 10)
               :sink   (a/chan 10)})

  (ws/connect "ws://localhost:3003/api/session" stream)

  (go-loop []
    (let [value (<! (:source stream))]
      (println value))
    (recur))

  (go (>! (:sink stream) "hello abc")))

