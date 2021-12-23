(ns todomvc-ratom.schedule
  (:require
   [fancoil.base :as base]))



(defmethod base/schedule :app/initialize
  [{:keys [process local-storage-key]} _method]
  (process :task/initialise-db {:local-storage-key local-storage-key})
  (process :task/backup-db {:local-storage-key local-storage-key}))
