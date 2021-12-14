(ns simple.core
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [fancoil.base :as base]
   [fancoil.core :as fc]
   [integrant.core :as ig]))


;; -----------------------------------------
;; handle event


(defmethod base/handle :initialize
  [_ _ _req]
  (let [new-db {:time (js/Date.)
                :time-color "#f88"}]
    {:ratom/reset new-db}))

(defmethod base/handle :time-color-change
  [_ _ {event :request/event db :ratom/db}]
  (let [{:keys [new-color-value]} event
        new-db (assoc db :time-color new-color-value)]
    {:ratom/reset new-db}))

(defmethod base/handle :timer
  [_ _ {event :request/event db :ratom/db}]
  (let [{:keys [new-time]} event
        new-db (assoc db :time new-time)]
    {:fx/doseq [{:ratom/reset new-db}
                {:log/out new-time}]}))


;; -----------------------------------------
;; handle!


(defmethod base/handle! :tictac
  [{:keys [doall! handle inject]} _ _]
  (let [tik-tok (fn []
                  (let [req (inject :ratom/db {:request/event {:new-time (js/Date.)}})
                        resp (handle :timer req)]
                    (doall! resp)))]
    (js/setInterval tik-tok 1000)))

(defmethod base/handle! :default
  [{:keys [doall! handle inject]} signal req]
  (let [req (inject :ratom/db req)
        resp (handle signal req)]
    (doall! resp)))


;; -----------------------------------------
;; subs


(defmethod base/subscribe :time
  [{:keys [ratom]} _ req]
  (r/cursor ratom [:time]))


(defmethod base/subscribe :time-color
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:time-color]))

;; -----------------------------------------
;; views

(defmethod base/view :clock
  [{:keys [subscribe]} _ props]
  (fn [props]
    [:div.example-clock
     {:style {:color @(subscribe :time-color)}}
     (-> @(subscribe :time)
         .toTimeString
         (str/split " ")
         first)]))


(defmethod base/view :color-input
  [{:keys [dispatch subscribe]} _ props]
  (fn [props]
    [:div.color-input
     "Time color: "
     [:input {:type "text"
              :value @(subscribe :time-color)
              :on-change #(dispatch :time-color-change {:new-color-value (-> % .-target .-value)})}]]))


(defmethod base/view :ui
  [config _ props]
  (fn [props]
    [:div
     [:h1 "Hello world, it is now"]
     [(base/view config :clock)]
     [(base/view config :color-input)]]))


;; -------------------------
;; integrant 

;; Make sure you know the default config, or you can write it from scratch

(def config
  {::fc/ratom {:initial-value {}}
   ::fc/tap {}
   ::fc/inject {:ratom (ig/ref ::fc/ratom)}
   ::fc/do! {:ratom (ig/ref ::fc/ratom)}
   ::fc/doall! {:do! (ig/ref ::fc/do!)}
   ::fc/handle {:tap (ig/ref ::fc/tap)}
   ::fc/handle! {:ratom (ig/ref ::fc/ratom)
                 :handle (ig/ref ::fc/handle)
                 :inject (ig/ref ::fc/inject)
                 :do! (ig/ref ::fc/do!)
                 :doall! (ig/ref ::fc/doall!)}
   ::fc/subscribe {:ratom (ig/ref ::fc/ratom)}
   ::fc/view {:dispatch (ig/ref ::fc/dispatch)
              :subscribe (ig/ref ::fc/subscribe)}
   ::fc/chan {}
   ::fc/dispatch {:event-chan (ig/ref ::fc/chan)}
   ::fc/service {:handle! (ig/ref ::fc/handle!)
                 :event-chan (ig/ref ::fc/chan)}})


;; or you can use user-config to merge default-config, make sure 
;; you known the default config well

#_(def config
  (let [user-config {::fc/ratom {:initial-value {}}}]
    (fc/merge-config fc/default-config user-config)))


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [handle! (::fc/handle! system)]
    (handle! :initialize)
    (handle! :tictac))

  (rdom/render [(::fc/view system) :ui]
               (js/document.getElementById "app")))


(defn ^:export init! []
  (mount-root))
