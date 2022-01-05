(ns todomvc-posh.test-sub
  (:require
   [todomvc-posh.core :as todo-core]
   [integrant.core :as ig]))

(def test-system
  (ig/init todo-core/config [::todo-core/pconn
                             ::todo-core/subscribe]))

(def subscribe
  (::todo-core/subscribe test-system))


@(subscribe :todolist/sub-one {:id [:todolist/name "default"]})
