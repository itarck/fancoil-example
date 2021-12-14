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


;; fancoil version

(defmethod ig/init-key ::init!
  [_k {:keys [local-storage-key handle!]}]
  (handle! :task/initialise-db {:local-storage-key local-storage-key})
  (handle! :task/backup-db {:local-storage-key local-storage-key}))


(def config
  {::fc/ratom {:initial-value db/default-db}   ; state!
   ::fc/tap {}
   ::fc/inject {:ratom (ig/ref ::fc/ratom)}   ;; user, state!
   ::fc/do! {:ratom (ig/ref ::fc/ratom)}   ; state!
   ::fc/doall! {:do! (ig/ref ::fc/do!)}
   ::fc/handle {:tap (ig/ref ::fc/tap)}
   ::fc/handle! {:ratom (ig/ref ::fc/ratom)
                 :handle (ig/ref ::fc/handle)
                 :inject (ig/ref ::fc/inject)
                 :do! (ig/ref ::fc/do!)
                 :doall! (ig/ref ::fc/doall!)}
   ::fc/subscribe {:ratom (ig/ref ::fc/ratom)}    ; user, state!
   ::fc/view {:dispatch (ig/ref ::fc/dispatch)    ;; user 
              :subscribe (ig/ref ::fc/subscribe)}   ;; user, state!
   ::fc/chan {}
   ::fc/dispatch {:event-chan (ig/ref ::fc/chan)}
   ::fc/service {:handle! (ig/ref ::fc/handle!)
                 :event-chan (ig/ref ::fc/chan)}
   ::init! {:local-storage-key "todomvc"
            :handle! (ig/ref ::fc/handle!)}})


(def system
  (ig/init config))



;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (dom/render ((::fc/view system) :todo-app {})
              (js/document.getElementById "app"))
  )

(defn ^:export init! []
  (mount-root))
