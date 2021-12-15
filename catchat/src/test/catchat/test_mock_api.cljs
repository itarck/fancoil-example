(ns catchat.test-mock-api
  (:require
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [catchat.core :as cc]))


(def system 
  (ig/init cc/config))

(def do! 
  (::fc/do! system))


(do! :mock-api/request
     {:uri "/api/whoami"
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