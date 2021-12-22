(ns catchat.core
  (:require
   [reagent.dom :as rdom]
   [integrant.core :as ig]
   [fancoil.unit :as fu]
   [fancoil.module.datascript.unit]

   [catchat.plugin.mock-api]
   [catchat.db :as db]
   [catchat.event]
   [catchat.process]
   [catchat.view]))


(derive ::conn :fancoil.module.datascript/conn)

(def config
  {::conn {:schema db/schema}
   ::fu/handle {}
   ::fu/inject {:conn (ig/ref ::conn)}
   ::fu/do! {:conn (ig/ref ::conn)
             :dispatch (ig/ref ::fu/dispatch)}
   ::fu/doall! {:do! (ig/ref ::fu/do!)}
   ::fu/handle! {:handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :doall! (ig/ref ::fu/doall!)}
   ::fu/service {:handle! (ig/ref ::fu/handle!)
                 :event-chan (ig/ref ::fu/chan)}
   ::fu/chan {}
   ::fu/dispatch {:event-chan (ig/ref ::fu/chan)}
   ::fu/view {:conn (ig/ref ::conn)
              :dispatch (ig/ref ::fu/dispatch)}})


(def system 
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root []
  (let [view (::fu/view system)
        dispatch (::fu/dispatch system)]
    (dispatch :room/get-rooms)
    (dispatch :user/load-whoami)
    (dispatch :init/sub-messages)
    (rdom/render (view :catchat/root)
                 (.getElementById js/document "app"))))

(defn ^:export init! []
  (mount-root))
