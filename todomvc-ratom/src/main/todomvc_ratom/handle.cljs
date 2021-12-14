(ns todomvc-ratom.handle
  (:require
   [fancoil.base :as base]))


;; handlers 

(defmethod base/handle :set-showing
  [{:keys [tap]} _ {event :request/event db :ratom/db}]
  (let [{:keys [new-filter-kw]} event
        db (tap :set-showing {:db db :new-filter-kw new-filter-kw})]
    {:ratom/reset db}))

(defmethod base/handle :add-todo
  [{:keys [tap]} _ {event :request/event db :ratom/db}]
  (let [new-db (tap :add-todo {:db db :text (:text event)})]
    {:ratom/reset new-db}))

(defmethod base/handle :toggle-done
  [{:keys [tap]} _ {event :request/event db :ratom/db}]
  (let [{:keys [id]} event
        new-db (tap :toggle-done {:db db :id id})]
    {:ratom/reset new-db}))

(defmethod base/handle :save
  [{:keys [tap]} _ {event :request/event db :ratom/db}]
  (let [{:keys [id title]} event
        new-db (tap :save-title {:db db :id id :title title})]
    {:ratom/reset new-db}))

(defmethod base/handle :delete-todo
  [{:keys [tap]} _ {event :request/event db :ratom/db}]
  (let [{:keys [id]} event
        new-db (tap :delete-todo {:id id :db db})]
    {:ratom/reset new-db}))

(defmethod base/handle :clear-completed
  [{:keys [tap]} _ {db :ratom/db}]
  {:ratom/reset (tap :clear-completed {:db db})})

(defmethod base/handle :complete-all-toggle
  [{:keys [tap]} _ {db :ratom/db}]
  {:ratom/reset (tap :complete-all-toggle {:db db})})


(comment

  (def tap (partial base/tap {}))
  (def handle (partial base/handle {:tap tap}))

  (def db (tap :add-todo {:text "abc"}))

  (handle :add-todo {:ratom/db db :event {:text "abc"}})
  (handle :save {:ratom/db db :event {:id 1 :title "hello"}})

  )
