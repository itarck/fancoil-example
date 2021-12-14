(ns todomvc-datascript.sub
  (:require
   [fancoil.base :as base]
   [posh.reagent :as p]))


;; todo

(defmethod base/subscribe :todo/pull-one
  [{:keys [pconn]} _sig {:keys [id]}]
  (p/pull pconn '[*] id))

;; todolist

(defmethod base/subscribe :todolist/pull-one
  [{:keys [pconn]} _sig {:keys [id]}]
  (p/pull pconn '[* {:todo/_todolist [*]}] id))

(defmethod base/subscribe :todolist/filter-todo-ids
  [{:keys [pconn]} _sig {:keys [todolist status]}]
  (p/q '[:find  [?tid ...]
         :in $ ?todolist-id ?status
         :where
         [?tid :todo/todolist ?todolist-id]
         [?tid :todo/status ?status]]
       pconn (:db/id todolist) status))

