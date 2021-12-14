(ns todomvc-datascript.test-handle
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [fancoil.core :as fc]
   [fancoil.base :as base]
   [todomvc-datascript.core :as todo-core]
   [todomvc-datascript.handle :as handle]
   [todomvc-datascript.db :as db]))


(def test-system
  (ig/init todo-core/config [::todo-core/pconn]))

(def pconn 
  (::todo-core/pconn test-system))

(def handle 
  (partial base/handle {}))

(def todolist 
  (d/pull @pconn '[* {:todo/_todolist [*]}] 1))


(handle :todo/set-status {:request/event {:id 1 :status :done}})

(handle :todolist/toggle-all {:request/event {:todolist todolist :status :done}})

(handle :todolist/clear-done {:request/event {:todolist todolist}
                              :posh/db @pconn})