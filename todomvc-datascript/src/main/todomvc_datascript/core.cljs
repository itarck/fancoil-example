(ns todomvc-datascript.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [integrant.core :as ig]

   [fancoil.core :as fc]
   [fancoil.lib.posh]
   
   [todomvc-datascript.db :as db]
   [todomvc-datascript.subs]))


(derive ::pconn :fancoil.lib/posh)
(derive ::subscribe :fancoil.core/subscribe)

(def config 
  {::pconn {:schema db/schema
            :initial-tx db/initial-tx}
   ::subscribe {:pconn (ig/ref ::pconn)}})


(def system 
    (ig/init config))

((::subscribe system) :todolist/sub-one {:id [:todolist/name "default"]})

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
