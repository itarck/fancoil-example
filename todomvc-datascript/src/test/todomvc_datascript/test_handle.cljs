(ns todomvc-datascript.test-handle
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [fancoil.core :as fc]
   [todomvc-datascript.core :as todo-core]))


(def test-system
  (ig/init todo-core/config [::todo-core/pconn
                             ::todo-core/handle]))

(def pconn 
  (::todo-core/pconn test-system))

(def handle 
  (::todo-core/handle test-system))

(def todolist 
  (d/pull @pconn '[* {:todo/_todolist [*]}] 1))


(handle :todo/set-status {:request/event {:id 1 :status :done}})

(handle :todolist/toggle-all {:request/event {:todolist todolist :status :done}})

(handle :todolist/clear-done {:request/event {:todolist todolist}
                              :posh/db @pconn})