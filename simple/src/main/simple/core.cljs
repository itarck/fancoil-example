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
  [env _ props]
  (fn [props]
    [:div
     [:h1 "Hello world, it is now"]
     [(base/view env :clock)]
     [(base/view env :color-input)]]))



;; integrant 

(def hierarchy
  {::ratom [::fc/ratom]
   ::inject [::fc/inject]
   ::do! [::fc/do!]
   ::doall! [::fc/doall!]
   ::handle [::fc/handle]
   ::handle! [::fc/handle!]
   ::subscribe [::fc/subscribe]
   ::view [::fc/view]})


(defmethod ig/init-key ::dispatch
  [_ {:keys [handle!]}]
  (fn [signal event]
    (handle! signal #:request{:event event})))


(def config
  {::ratom {:initial-value {}}
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
  (let [_ (fc/load-hierarchy hierarchy)]
    (ig/init config)))


;; -------------------------
;; Initialize app

(defn mount-root
  []
  (let [handle! (::handle! system)]
    (handle! :initialize)
    (handle! :tictac))

  (rdom/render [(::view system) :ui]
                      (js/document.getElementById "app")))


(defn ^:export init! []
  (mount-root))
