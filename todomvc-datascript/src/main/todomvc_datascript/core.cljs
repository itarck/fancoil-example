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


(derive ::pconn :fancoil.lib/posh)
(derive ::subscribe :fancoil.core/subscribe)
(derive ::handle :fancoil.core/handle)
(derive ::handle! :fancoil.core/handle!)
(derive ::do! ::fc/do!)
(derive ::inject ::fc/inject)
(derive ::doall! ::fc/doall!)
(derive ::event-chan ::fc/chan)
(derive ::view ::fc/view)
(derive ::dispatch ::fc/dispatch)
(derive ::service ::fc/service)


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
    (ig/init config))



;; -------------------------
;; Initialize app

(defn mount-root []
  (let [view (::view system)
        todolist {:db/id [:todolist/name "default"]}]
    (d/render (view :todolist/view {:todolist todolist})
              (.getElementById js/document "app"))))


(defn ^:export init! []
  (mount-root))
