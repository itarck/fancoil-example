(ns todomvc-ratom.core
  (:require
   [fancoil.core :as fc]
   [fancoil.unit :as fu]
   [fancoil.base :as base]
   [integrant.core :as ig]
   [reagent.dom :as dom]
   [todomvc-ratom.plugin.local-storage]
   [todomvc-ratom.db :as db]
   [todomvc-ratom.event]
   [todomvc-ratom.sub]
   [todomvc-ratom.view]
   [todomvc-ratom.process]
   [todomvc-ratom.schedule]))


(def user-config
  {::fu/ratom {:initial-value db/default-db}
   ::fu/schedule {:local-storage-key "todomvc"
                  :process (ig/ref ::fu/process)}})

;; Use default config 
;; Please read it before you use

(def config
  (fc/merge-config fc/default-config user-config))


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root []
  (let [schedule (::fu/schedule system)]
    (schedule :app/initialize))
  (dom/render ((::fu/view system) :todo-app {})
              (js/document.getElementById "app")))

(defn ^:export init! []
  (mount-root))
