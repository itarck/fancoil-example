(ns todomvc-posh.db
  (:require
   [fancoil.module.posh.base :as posh.base]))

;; config schema to posh/pconn unit

(def schema
  {:todolist/name {:db/unique :db.unique/identity}
   :todolist/todo-template {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}
   :todo/todolist {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; or use posh/schema unit

(defmethod posh.base/schema :todolist/schema
  [config _ _]
  {:todolist/name {:db/unique :db.unique/identity}
   :todolist/todo-template {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})

(defmethod posh.base/schema :todo/schema
  [config _ _]
  {:todo/todolist {:db/valueType :db.type/ref :db/cardinality :db.cardinality/one}})


;; initial-tx

(def initial-tx
  [{:db/id            -4
    :todolist/name "default"
    :todolist/todo-template -1
    :todolist/description "a todolist"}
   {:db/id            -1
    :todo/title       ""
    :todo/description ""
    :todo/status      :new}
   {:db/id            -2
    :todo/title       "Learn Clojure more"
    :todo/description "Just learn it"
    :todo/status      :active
    :todo/todolist [:todolist/name "default"]}
   {:db/id            -3
    :todo/title       "Have a coffee"
    :todo/description "Just relax"
    :todo/status      :active
    :todo/todolist [:todolist/name "default"]}])
