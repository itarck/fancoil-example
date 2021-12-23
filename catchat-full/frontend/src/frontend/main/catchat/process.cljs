(ns catchat.process
  (:require
   [fancoil.base :as base]))


(defmethod base/process :default
  [{:keys [do! handle inject]} method req]
  (let [req (inject :ds/db req)
        resp (handle method req)]
    (do! :do/effect resp)))

