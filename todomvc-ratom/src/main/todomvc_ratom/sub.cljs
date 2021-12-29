(ns todomvc-ratom.sub
  (:require 
   [reagent.core :as r]
   [fancoil.base :as base]))


(defmethod base/subscribe :showing
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:showing]))


(defmethod base/subscribe :sorted-todos
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:todos]))


(defmethod base/subscribe :todos
  [core _ _]
  (let [sorted-todos @(base/subscribe core :sorted-todos)]
    (r/reaction (vals sorted-todos))))


(defmethod base/subscribe :visible-todos
  [core _ _]
  (let [todos @(base/subscribe core :todos)
        showing @(base/subscribe core :showing)
        filter-fn (case showing
                    :active (complement :done)
                    :done   :done
                    :all    identity
                    identity)]
    (r/reaction (filter filter-fn todos))))


(defmethod base/subscribe :all-complete?
  [core _ _]
  (let [todos @(base/subscribe core :todos)]
    (r/reaction (every? :done todos))))


(defmethod base/subscribe :completed-count
  [core _ _]
  (let [todos @(base/subscribe core :todos)]
    (r/reaction (count (filter :done todos)))))


(defmethod base/subscribe :footer-counts
  [core _ _]
  (let [todos @(base/subscribe core :todos)
        completed-count @(base/subscribe core :completed-count)]
    (r/reaction [(- (count todos) completed-count) completed-count])))

 