(ns todomvc-ratom.core
  (:require
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [reagent.core :as r]
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
  {::fc/ratom {:initial-value db/default-db}
   ::init! {:local-storage-key "todomvc"
            :handle! (ig/ref ::fc/handle!)}})

;; Use default config 
;; Please read it before you use

(def config
  (fc/merge-config fc/default-config user-config))


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root []
  (dom/render ((::fc/view system) :todo-app {})
              (js/document.getElementById "app"))
  )

(defn ^:export init! []
  (mount-root))
