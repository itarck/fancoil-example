(ns todomvc-datascript.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [integrant.core :as ig]

   [fancoil.core :as fc]
   [fancoil.lib.posh]
   
   [todomvc-datascript.db :as db]
   [todomvc-datascript.sub]
   [todomvc-datascript.handle]
   [todomvc-datascript.process]
   [todomvc-datascript.view]))


(def hierarchy
  {::pconn [:fancoil.lib/posh]
   ::subscribe [::fc/subscribe]
   ::handle [::fc/handle]
   ::handle! [::fc/handle!]
   ::do! [::fc/do!]
   ::inject [::fc/inject]
   ::doall! [::fc/doall!]
   ::event-chan [::fc/chan]
   ::view [::fc/view]
   ::dispatch [::fc/dispatch]
   ::service [::fc/service]})

(def config
  {::pconn {:schema db/schema
            :initial-tx db/initial-tx}
   ::subscribe {:pconn (ig/ref ::pconn)}
   ::inject {:pconn (ig/ref ::pconn)}
   ::do! {:pconn (ig/ref ::pconn)}
   ::doall! {:do! (ig/ref ::do!)}
   ::handle {}
   ::handle! {:inject (ig/ref ::inject)
              :doall! (ig/ref ::doall!)
              :handle (ig/ref ::handle)}
   ::view {:dispatch (ig/ref ::dispatch)
           :subscribe (ig/ref ::subscribe)} 
   ::event-chan {}
   ::dispatch {:event-chan (ig/ref ::event-chan)}
   ::service {:handle! (ig/ref ::handle!)
              :event-chan (ig/ref ::event-chan)}})


(def system
  (let [_ (fc/load-hierarchy hierarchy)]
    (ig/init config)))


;; -------------------------
;; Initialize app

(defn mount-root []
  (let [view (::view system)
        todolist {:db/id [:todolist/name "default"]}]
    (d/render (view :todolist/view {:todolist todolist})
              (.getElementById js/document "app"))))


(defn ^:export init! []
  (mount-root))
