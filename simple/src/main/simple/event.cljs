(ns simple.event
  (:require 
   [fancoil.base :as base]))


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


(comment

  (base/handle :initialize)
  
  )