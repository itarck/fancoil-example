(ns simple.sub
  (:require
   [reagent.core :as r]
   [fancoil.base :as base]))


(defmethod base/subscribe :time
  [{:keys [ratom]} _ req]
  (r/cursor ratom [:time]))


(defmethod base/subscribe :time-color
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:time-color]))