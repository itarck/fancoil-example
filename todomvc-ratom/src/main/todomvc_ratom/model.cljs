(ns todomvc-ratom.model
  (:require
   [cljs.spec.alpha :as s]
   [fancoil.base :as base]))


(defn check-and-throw
  "Throws an exception if `db` doesn't match the Spec `a-spec`."
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(defn allocate-next-id
  "Returns the next todo id.
  Assumes todos are sorted.
  Returns one more than the current largest id."
  [todos]
  ((fnil inc 0) (last (keys todos))))

(defmethod base/tap :set-showing
  [_env _sig {:keys [db new-filter-kw]}]     ;; new-filter-kw is one of :all, :active or :done
  (assoc db :showing new-filter-kw))

(defmethod base/tap :add-todo
  [_env _sig {:keys [db text]}]
  (let [id (allocate-next-id (:todos db))
        new-todo {:id id :title text :done false}]
    (assoc-in db [:todos id] new-todo)))

(defmethod base/tap :toggle-done
  [_env _sig {:keys [db id]}]
  (update-in db [:todos id :done] not))

(defmethod base/tap :save-title
  [_env _sig {:keys [db id title]}]
  (assoc-in db [:todos id :title] title))

(defmethod base/tap :delete-todo
  [_env _sig {:keys [db id]}]
  (update-in db [:todos] dissoc id))

(defmethod base/tap :clear-completed
  [_env _sig {:keys [db]}]
  (let [todos (:todos db)
        done-ids (->> (vals todos)
                      (filter :done)
                      (map :id))
        new-todos (reduce dissoc todos done-ids)]
    (assoc db :todos new-todos)))

(defmethod base/tap :complete-all-toggle
  [_env _sig {:keys [db]}]
  (let [todos (:todos db)
        new-done (not-every? :done (vals todos))
        new-todos (reduce #(assoc-in %1 [%2 :done] new-done)
                          todos
                          (keys todos))]   ;; work out: toggle true or false?
    (assoc db :todos new-todos)))


(comment 
  
  (def tap 
    (partial base/tap {}))
  
  (def db
    {:todos {1 {:id 1, :title "abc", :done false}}})
  
  (tap :add-todo {:db db :text "abc"})
  (tap :toggle-done {:db db :id 1})

  )