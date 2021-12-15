(ns catchat.test-handle!
  (:require 
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [catchat.core :as cc]))


(def system 
  (ig/init cc/config))


(def handle! 
  (::fc/handle! system))

(def conn 
  (::cc/conn system))

(handle! :room/get-rooms)
(handle! :user/load-whoami)

@conn