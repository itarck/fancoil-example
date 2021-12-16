(ns todomvc-ratom.core
  (:require
   [fancoil.core :as fc]
   [fancoil.unit :as fu]
   [integrant.core :as ig]
   [reagent.dom :as dom]
   [todomvc-ratom.plugin.local-storage]
   [todomvc-ratom.db :as db]
   [todomvc-ratom.model]
   [todomvc-ratom.handle]
   [todomvc-ratom.sub]
   [todomvc-ratom.view]
   [todomvc-ratom.process]))


(defmethod ig/init-key ::init!
  [_k {:keys [local-storage-key handle!]}]
  (handle! :task/initialise-db {:local-storage-key local-storage-key})
  (handle! :task/backup-db {:local-storage-key local-storage-key}))


(def user-config
  {::fu/ratom {:initial-value db/default-db}
   ::init! {:local-storage-key "todomvc"
            :handle! (ig/ref ::fu/handle!)}})

;; Use default config 
;; Please read it before you use

(def config
  (fc/merge-config fc/default-config user-config))


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root []
  (dom/render ((::fu/view system) :todo-app {})
              (js/document.getElementById "app"))
  )

(defn ^:export init! []
  (mount-root))
