(ns catchat.core
    (:require
     [reagent.dom :as rdom]
     [integrant.core :as ig]
     [fancoil.core :as fc]
     [fancoil.lib.datascript]

     [catchat.plugin.mock-api]
     [catchat.db :as db]
     [catchat.handle]
     [catchat.process]
     [catchat.view]))


(derive ::conn :fancoil.lib/datascript)

(def config
  {::conn {:schema db/schema}
   ::fc/handle {}
   ::fc/inject {:conn (ig/ref ::conn)}
   ::fc/do! {:conn (ig/ref ::conn)
             :dispatch (ig/ref ::fc/dispatch)}
   ::fc/doall! {:do! (ig/ref ::fc/do!)}
   ::fc/handle! {:handle (ig/ref ::fc/handle)
                 :inject (ig/ref ::fc/inject)
                 :doall! (ig/ref ::fc/doall!)}
   ::fc/service {:handle! (ig/ref ::fc/handle!)
                 :event-chan (ig/ref ::fc/chan)}
   ::fc/chan {}
   ::fc/dispatch {:event-chan (ig/ref ::fc/chan)}
   ::fc/view {:conn (ig/ref ::conn)
              :event-bus (ig/ref ::fc/chan)}})


(def system 
  (ig/init config))



;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render ((::fc/view system) :catchat/root)
               (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
