(ns simple.view
  (:require
   [clojure.string :as str]
   [fancoil.base :as fcb]))


(defmethod fcb/view :clock
  [{:keys [subscribe]} _ props]
  (fn [props]
    [:div.example-clock
     {:style {:color @(subscribe :time-color)}}
     (-> @(subscribe :time)
         .toTimeString
         (str/split " ")
         first)]))


(defmethod fcb/view :color-input
  [{:keys [dispatch subscribe]} _ props]
  (fn [props]
    [:div.color-input
     "Time color: "
     [:input {:type "text"
              :value @(subscribe :time-color)
              :on-change #(dispatch :time-color-change {:new-color-value (-> % .-target .-value)})}]]))


(defmethod fcb/view :ui
  [env _ props]
  (fn [props]
    [:div
     [:h1 "Hello world, it is now"]
     [(fcb/view env :clock)]
     [(fcb/view env :color-input)]]))




