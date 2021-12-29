(ns catchat.core
  (:require
   [reagent.dom :as rdom]
   [integrant.core :as ig]
   [fancoil.unit :as fu]
   [fancoil.module.datascript.unit]

   [catchat.plugin.mock-api]
   [catchat.db :as db]
   [catchat.handle]
   [catchat.process]
   [catchat.view]))


(derive ::conn :fancoil.module.datascript/conn)

(def config
  {::conn {:schema db/schema}
   ::fu/handle {}
   ::fu/inject {:conn (ig/ref ::conn)}
   ::fu/do! {:conn (ig/ref ::conn)
             :dispatch (ig/ref ::fu/dispatch)}
   ::fu/process {:handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/view {:conn (ig/ref ::conn)
              :dispatch (ig/ref ::fu/dispatch)}})


(def system 
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root []
  (let [view (::fu/view system)
        dispatch (::fu/dispatch system)]
    (dispatch :room/get-rooms {})
    (dispatch :user/load-whoami {})
    (dispatch :init/sub-messages {})
    (rdom/render (view :catchat/root)
                 (.getElementById js/document "app"))))

(defn ^:export init! []
  (mount-root))
