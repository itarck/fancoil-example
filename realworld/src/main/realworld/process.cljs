(ns realworld.process
  (:require
   [fancoil.base :as base]))


(defmethod base/process :default
  [{:keys [do! handle inject]} method req]
  (let [req (->> req
                 (inject :ratom/db)
                 (inject :router/route))
        resp (handle method req)]
    (do! :do/effect resp)))
