(ns catchat.test-api
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [go <! go-loop] :as a]
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [catchat.core :as cc]
   [haslett.client :as ws]))


(go
  (println (<! (http/get "/api/get-user/7" {}))))


(def system
  (ig/init cc/config))

(def do!
  (::fc/do! system))


(do! :chat-session/send! {:hello "world"})


(do! :api/post
     {:uri "/api/get-rooms"
      :callback println})


(do! :mock-api/request
     {:uri "/api/get-rooms"
      :callback println})

(do! :api/post
     {:uri "/api/get-user"
      :body "7"
      :callback println})


(do! :mock-api/request
     {:uri "/api/send"
      :body {:message/text "abc"}
      :callback println})


(comment

  (def stream {:source (a/chan 10)
               :sink   (a/chan 10)})

  (ws/connect "ws://localhost:3003/api/session" stream)

  (go-loop []
    (let [value (<! (:source stream))]
      (println value))
    (recur))

  (go (>! (:sink stream) "hello abc"))

  ;; 
  )