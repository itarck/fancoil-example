(ns todomvc-datascript.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [integrant.core :as ig]
   [fancoil.core :as fc]
   [fancoil.lib.posh :as lib.posh]
   
   [todomvc-datascript.db :as db]))


(derive ::pconn :fancoil.lib/posh)

(def config 
  {::pconn {:schema db/schema
            :initial-tx db/initial-tx}})


(def system 
    (ig/init config))

system

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
