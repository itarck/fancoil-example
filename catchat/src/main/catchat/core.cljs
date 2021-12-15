(ns catchat.core
    (:require
     [reagent.dom :as rdom]
     [integrant.core :as ig]
     [fancoil.core :as fc]
     [fancoil.lib.datascript]
     [catchat.plugin.mock-api]
     [catchat.db :as db]
     ))


(def config
  {:fancoil.lib/datascript {:schema db/schema}
   ::fc/do! {:conn (ig/ref :fancoil.lib/datascript)}
   ::fc/chan {}
   ::fc/dispatch {:event-chan (ig/ref ::fc/chan)}})


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
