(ns simple.core
  (:require
   [reagent.dom]
   [fancoil.core :as fc]
   [integrant.core :as ig]
   [simple.event]
   [simple.sub]
   [simple.view]
   [simple.process]))


;; fancoil version 

(derive ::ratom ::fc/ratom)
(derive ::inject ::fc/inject)
(derive ::do! ::fc/do!)
(derive ::doall! ::fc/doall!)
(derive ::handle ::fc/handle)
(derive ::handle! ::fc/handle!)
(derive ::subscribe ::fc/subscribe)
(derive ::view ::fc/view)


(defmethod ig/init-key ::dispatch
  [_ {:keys [handle!]}]
  (fn [signal event]
    (handle! signal #:request{:event event})))


(def config
  {::ratom {}
   ::inject {:ratom (ig/ref ::ratom)}
   ::do! {:ratom (ig/ref ::ratom)}
   ::doall! {:do! (ig/ref ::do!)}
   ::handle {}
   ::handle! {:doall! (ig/ref ::doall!)
              :handle (ig/ref ::handle)
              :inject (ig/ref ::inject)}
   ::dispatch {:handle! (ig/ref ::handle!)}
   ::subscribe {:ratom (ig/ref ::ratom)}
   ::view {:dispatch (ig/ref ::dispatch)
           :subscribe (ig/ref ::subscribe)}})


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [handle! (::handle! system)]
    (handle! :initialize)
    (handle! :tictac))

  (reagent.dom/render [(::view system) :ui]
                      (js/document.getElementById "app")))


(defn ^:export init! []
  (mount-root))
