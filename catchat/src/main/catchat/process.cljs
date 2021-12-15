(ns catchat.process
  (:require
   [fancoil.base :as base]))


(defmethod base/handle! :default
  [{:keys [doall! handle inject]} signal req]
  (let [req (inject :ds/db req)
        resp (handle signal req)]
    (doall! resp)))

