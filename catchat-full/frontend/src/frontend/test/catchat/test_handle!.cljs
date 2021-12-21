(ns catchat.test-handle!
  (:require 
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [catchat.core :as cc]))


#_(def system 
  (ig/init cc/config))

(def system 
  cc/system
  )

(def handle! 
  (::fc/handle! system))

(def conn 
  (::cc/conn system))

(handle! :room/get-rooms)
(handle! :user/load-whoami)

@conn