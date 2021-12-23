(ns todomvc-datascript.handle
  (:require
   [datascript.core :as d]
   [fancoil.base :as base]))

;; todo

(defmethod base/handle :todo/set-status
  [_ _ {{:keys [id status]} :request/event}]
  {:posh/tx [[:db/add id :todo/status status]]})

(defmethod base/handle :todo/delete
  [_ _ {{:keys [id]} :request/event}]
  {:posh/tx [[:db/retractEntity id]]})

(defmethod base/handle :todo/toggle-status
  [_ _ {{:keys [todo]} :request/event}]
  (let [new-status (if (= :active (:todo/status todo)) :done :active)]
    {:posh/tx [[:db/add (:db/id todo) :todo/status new-status]]}))

(defmethod base/handle :todo/set-title
  [_ _ {{:keys [id value]} :request/event}]
  {:posh/tx [[:db/add id :todo/title value]]})


;; todolist 

(defmethod base/handle :todolist/create-todo
  [_ _ {event :request/event}]
  (let [{:keys [todolist title]} event
        template-id (:db/id (:todolist/todo-template todolist))]
    {:posh/tx [[:db/add template-id :todo/title ""]
               {:db/id       -1
                :todo/title  title
                :todo/status  :active
                :todo/todolist (:db/id todolist)}]}))

(defmethod base/handle :todolist/toggle-all
  [_ _ {event :request/event}]
  (let [{:keys [todolist status]} event]
    {:posh/tx (vec (for [todo (:todo/_todolist todolist)]
                     [:db/add (:db/id todo) :todo/status status]))}))

(defmethod base/handle :todolist/clear-done
  [_ _ {event :request/event
        db :posh/db}]
  (let [{:keys [todolist]} event
        todos (d/pull-many db '[*] (map :db/id (:todo/_todolist todolist)))
        done (filter #(= :done (:todo/status %)) todos)]
    {:posh/tx (vec (for [todo done] [:db/retractEntity (:db/id todo)]))}))
