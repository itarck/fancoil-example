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
  [env _ _]
  (let [sorted-todos @(base/subscribe env :sorted-todos)]
    (r/reaction (vals sorted-todos))))


(defmethod base/subscribe :visible-todos
  [env _ _]
  (let [todos @(base/subscribe env :todos)
        showing @(base/subscribe env :showing)
        filter-fn (case showing
                    :active (complement :done)
                    :done   :done
                    :all    identity
                    identity)]
    (r/reaction (filter filter-fn todos))))


(defmethod base/subscribe :all-complete?
  [env _ _]
  (let [todos @(base/subscribe env :todos)]
    (r/reaction (every? :done todos))))


(defmethod base/subscribe :completed-count
  [env _ _]
  (let [todos @(base/subscribe env :todos)]
    (r/reaction (count (filter :done todos)))))


(defmethod base/subscribe :footer-counts
  [env _ _]
  (let [todos @(base/subscribe env :todos)
        completed-count @(base/subscribe env :completed-count)]
    (r/reaction [(- (count todos) completed-count) completed-count])))

 