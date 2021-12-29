(ns todomvc-datascript.core
  (:require
   [reagent.dom :as d]
   [integrant.core :as ig]

   [fancoil.core :as fc]
   [fancoil.unit :as fu]
   [fancoil.module.posh.unit]
   
   [todomvc-datascript.db :as db]
   [todomvc-datascript.sub]
   [todomvc-datascript.handle]
   [todomvc-datascript.process]
   [todomvc-datascript.view]))


(def hierarchy
  {::schema [:fancoil.module.posh/schema] 
   ::pconn [:fancoil.module.posh/pconn]})


(def config
  {::schema {}
   ::pconn {:schema (ig/ref ::schema)
            :initial-tx db/initial-tx}
   ::fu/subscribe {:pconn (ig/ref ::pconn)}
   ::fu/inject {:pconn (ig/ref ::pconn)}
   ::fu/do! {:pconn (ig/ref ::pconn)}
   ::fu/handle {}
   ::fu/process {:inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)
                 :handle (ig/ref ::fu/handle)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}})


(defonce system
  (let [_ (fc/load-hierarchy hierarchy)]
    (ig/init config)))


;; -------------------------
;; Initialize app

(defn mount-root []
  (let [view (::fu/view system)
        todolist {:db/id [:todolist/name "default"]}]
    (d/render (view :todolist/view {:todolist todolist})
              (.getElementById js/document "app"))))


(defn ^:export init! []
  (mount-root))


(comment

  (def test-system
    (let [_ (fc/load-hierarchy hierarchy)]
      (ig/init config [::schema])))

  (keys (::schema test-system))

  (println @(::pconn system))

  )