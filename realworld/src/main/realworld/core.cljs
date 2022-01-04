(ns realworld.core
  (:require
   [reagent.dom :as rdom]
   [integrant.core :as ig]
   [fancoil.unit :as fu]
   [fancoil.module.cljs-ajax.plugin]
   [fancoil.module.reitit.html-router.unit]
   [realworld.process]
   [realworld.sub]
   [realworld.view]
   [realworld.handle.api]
   [realworld.handle.page]))


;; -------------------------
;; integrant 

;; you can write it from scratch

(def routes
  [["/" :page/home-page]
   ["/profile/:username" :page/profile-page]
   ["/profile/:username/favorites" :page/profile-page-favorites]
   ["/article/:slug" :page/article-page]
   ["/login" :page/login-page]
   ["/logout" :page/logout-page]
   ["/register" :page/register-page]
   ["/settings" :page/settings-page]
   ["/editor" :page/editor-new-page]
   ["/editor/:slug" :page/editor-article-page]])


(derive ::router :fancoil.module.reitit/html-router)

(def config
  {::fu/ratom {:initial-value {}}
   ::fu/inject {:ratom (ig/ref ::fu/ratom)
                :router (ig/ref ::router)}
   ::fu/do! {:ratom (ig/ref ::fu/ratom)
             :dispatch (ig/ref ::fu/dispatch)
             :router (ig/ref ::router)}
   ::fu/handle {}
   ::fu/process {:ratom (ig/ref ::fu/ratom)
                 :handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)}
   ::fu/subscribe {:ratom (ig/ref ::fu/ratom)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)
              :router (ig/ref ::router)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/schedule {:dispatch (ig/ref ::fu/dispatch)}
   ::router {:routes routes
             :dispatch (ig/ref ::fu/dispatch)
             :on-navigate-request :router/on-navigate}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}})


(defonce system
  (ig/init config))


;; -------------------------
;; Initialize app


(defn mount-root
  []
  (rdom/render [(::fu/view system) :app/current-page]
               (js/document.getElementById "app")))


(defn ^:export init []
  (mount-root))


(comment 
  (keys system)
  )