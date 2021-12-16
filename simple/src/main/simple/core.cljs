(ns simple.core
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [fancoil.base :as base]
   [fancoil.unit :as fu]
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
  {::fu/ratom {:initial-value {}}
   ::fu/tap {}
   ::fu/inject {:ratom (ig/ref ::fu/ratom)}
   ::fu/do! {:ratom (ig/ref ::fu/ratom)}
   ::fu/doall! {:do! (ig/ref ::fu/do!)}
   ::fu/handle {:tap (ig/ref ::fu/tap)}
   ::fu/handle! {:ratom (ig/ref ::fu/ratom)
                 :handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)
                 :doall! (ig/ref ::fu/doall!)}
   ::fu/subscribe {:ratom (ig/ref ::fu/ratom)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)}
   ::fu/chan {}
   ::fu/dispatch {:event-chan (ig/ref ::fu/chan)}
   ::fu/service {:handle! (ig/ref ::fu/handle!)
                 :event-chan (ig/ref ::fu/chan)}})


;; or you can use user-config to merge default-config, make sure 
;; you known the default config well

#_(def config
  (let [user-config {::fu/ratom {:initial-value {}}}]
    (fc/merge-config fc/default-config user-config)))


(def system
  (ig/init config))


;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [handle! (::fu/handle! system)]
    (handle! :initialize)
    (handle! :tictac))

  (rdom/render [(::fu/view system) :ui]
               (js/document.getElementById "app")))


(defn ^:export init! []
  (mount-root))
