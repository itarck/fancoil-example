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
   [todomvc-datascript.process]))


(derive ::pconn :fancoil.lib/posh)
(derive ::subscribe :fancoil.core/subscribe)
(derive ::handle :fancoil.core/handle)
(derive ::handle! :fancoil.core/handle!)
(derive ::do! ::fc/do!)
(derive ::inject ::fc/inject)
(derive ::doall! ::fc/doall!)


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
              :handle (ig/ref ::handle)}})


(def system 
    (ig/init config))


;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to Reagent"]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
