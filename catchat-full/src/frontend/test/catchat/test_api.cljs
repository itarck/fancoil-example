(ns catchat.test-api
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [go <!]]
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [catchat.core :as cc]))


#_(go 
  (println (<! (http/post "/api/get-rooms" {}))))


(def system
  (ig/init cc/config))

(def do!
  (::fc/do! system))


(do! :api/post
     {:uri "/api/get-rooms"
      :callback println})


(do! :mock-api/request
     {:uri "/api/get-rooms"
      :callback println})

(do! :mock-api/request
     {:uri "/api/get-user"
      :body [7]
      :callback println})


(do! :mock-api/request
     {:uri "/api/send"
      :body {:message/text "abc"}
      :callback println})