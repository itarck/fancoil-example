(ns todomvc-datascript.test-process
  (:require
   [datascript.core :as d]
   [integrant.core :as ig]
   [fancoil.core :as fc]
   [todomvc-datascript.core :as todo-core]))


(def test-system
  (ig/init todo-core/config))

(def pconn
  (::todo-core/pconn test-system))

(def handle!
  (::todo-core/handle! test-system))

(def subscribe 
  (::todo-core/subscribe test-system))

(handle! :todo/set-status {:request/event {:id 2 :status :done}})

@(subscribe :todo/sub-one {:id 2})
;; => {:db/id 2, :todo/description "", :todo/status :done, :todo/title ""}

(def todolist 
  @(subscribe :todolist/sub-one {:id 1}))

