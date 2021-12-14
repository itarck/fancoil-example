(ns simple.process
  (:require
   [fancoil.base :as base]))

(defmethod base/handle! :tictac
  [{:keys [doall! handle inject]} _ _]
  (let [tik-tok (fn []
                  (let [req (inject :ratom/db {:request/event {:new-time (js/Date.)}})
                        resp (handle :timer req)]
                    (doall! resp)))]
    (js/setInterval tik-tok 1000)))

(defmethod base/handle! :default
  [{:keys [doall! handle inject]} signal req]
  (let [req (inject :ratom/db req)
        resp (handle signal req)]
    (doall! resp)))





