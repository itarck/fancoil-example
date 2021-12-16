(ns catchat.haslett
  (:require
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go go-loop >! <!] :as a]
   [haslett.client :as ws]
   [integrant.core :as ig]
   [fancoil.base :as base]))


(defmethod ig/init-key :catchat/haslett
  [_ {:keys [socket dispatch-signal dispatch]}]
  (let [stream {:source (a/chan 10)
                :sink   (a/chan 10)}]
    (ws/connect socket stream)
    (go-loop []
      (let [value (<! (:source stream))]
        (dispatch dispatch-signal (read-string value)))
      (recur))
    stream))

(defmethod base/do! :haslett/send!
  [{:keys [haslett]} _ event]
  (go (>! (:sink haslett) (str event))))


(comment

  (def stream {:source (a/chan 10)
               :sink   (a/chan 10)})

  (ws/connect "ws://localhost:3003/api/session" stream)

  (go-loop []
    (let [value (<! (:source stream))]
      (println value))
    (recur))

  (go (>! (:sink stream) "hello abc")))

