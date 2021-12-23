(ns todomvc-ratom.process
  (:require
   [fancoil.base :as base]))


(defmethod base/process :task/initialise-db
  [{:keys [inject do!]} _ task]
  (let [{:keys [local-storage-key]} task
        injected-req (inject :local-storage/load-entity task {})
        local-todos (get-in injected-req [:local-storage/entity local-storage-key])
        {:ratom/keys [db]} (inject :ratom/db)
        new-db (assoc db :todos local-todos)]
    (do! :ratom/reset new-db )))


(defmethod base/process :task/backup-db
  [{:keys [ratom do!]} _ {:keys [local-storage-key]}]
  (add-watch ratom :backup-db
             (fn [key atom old-db new-db]
               (do! :local-storage/save-entity {:local-storage-key local-storage-key
                                                :entity (:todos new-db)}))))


