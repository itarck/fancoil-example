(ns catchat.core
    (:require
     [reagent.dom :as rdom]
     [fancoil.core]
     [fancoil.lib.datascript]
     [integrant.core :as ig]
     
     [catchat.db :as db]))


(def config 
  {:fancoil.lib/datascript {:schema db/schema}})

(def system 
  (ig/init config))


;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
