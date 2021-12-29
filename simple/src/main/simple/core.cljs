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
;; handle 


(defmethod base/handle :app/initialize
  [_ _ _req]
  (let [new-db {:time (js/Date.)
                :time-color "#f88"}]
    {:ratom/reset new-db}))

(defmethod base/handle :clock/time-color-change
  [_ _ {body :request/body db :ratom/db}]
  (let [{:keys [new-color-value]} body
        new-db (assoc db :time-color new-color-value)]
    {:ratom/reset new-db}))

(defmethod base/handle :clock/timer
  [_ _ {body :request/body db :ratom/db}]
  (let [{:keys [new-time]} body
        new-db (assoc db :time new-time)]
    {:do/effects [{:ratom/reset new-db}
                  {:log/out new-time}]}))


;; -----------------------------------------
;; subs

(defmethod base/subscribe :clock/time
  [{:keys [ratom]} _ req]
  (r/cursor ratom [:time]))

(defmethod base/subscribe :clock/time-string
  [{:keys [ratom] :as config} _ req]
  (let [time @(base/subscribe config :clock/time req)]
    (r/reaction (if time
                  (-> (.toTimeString time)
                      (str/split " ")
                      first)
                  ""))))

(defmethod base/subscribe :clock/time-color
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:time-color]))


;; -----------------------------------------
;; views

(defmethod base/view :clock/timer
  [{:keys [subscribe]} _ props]
  (let [time-string @(subscribe :clock/time-string)
        color @(subscribe :clock/time-color)]
    [:div.example-clock
     {:style {:color color}}
     time-string]))


(defmethod base/view :clock/color-input
  [{:keys [dispatch subscribe]} _ props]
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(subscribe :clock/time-color)
            :on-change #(dispatch :clock/time-color-change {:new-color-value (-> % .-target .-value)})}]])


(defmethod base/view :app/ui
  [config _ props]
  [:div
   [:h1 "Hello world, it is now"]
   (base/view config :clock/timer)
   (base/view config :clock/color-input)])


;; -----------------------------------------
;; schedule


(defmethod base/schedule :clock/start-tictac
  [{:keys [dispatch]} _ {:keys [interval]}]
  (let [tictac (fn [] (dispatch :clock/timer {:new-time (js/Date.)}))]
    (js/setInterval tictac interval)))


;; -------------------------
;; integrant 

;; you can write it from scratch

(def config
  {::fu/ratom {:initial-value {}}
   ::fu/inject {:ratom (ig/ref ::fu/ratom)}
   ::fu/do! {:ratom (ig/ref ::fu/ratom)}
   ::fu/handle {}
   ::fu/process {:ratom (ig/ref ::fu/ratom)
                 :handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)}
   ::fu/subscribe {:ratom (ig/ref ::fu/ratom)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)
              :schedule (ig/ref ::fu/schedule)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/schedule {:dispatch (ig/ref ::fu/dispatch)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}})


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
  (let [dispatch (::fu/dispatch system)
        schedule (::fu/schedule system)]
    (dispatch :app/initialize {:sync? true})
    (schedule :clock/start-tictac {:interval 1000}))
  
  (rdom/render [(::fu/view system) :app/ui]
               (js/document.getElementById "app")))


(defn ^:export init! []
  (mount-root))

