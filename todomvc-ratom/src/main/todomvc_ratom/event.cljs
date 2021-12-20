(ns todomvc-ratom.event
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

;; handlers 

(defmethod base/handle :set-showing
  [_config _ {event :request/event db :ratom/db}]
  (let [{:keys [new-filter-kw]} event
        db (assoc db :showing new-filter-kw)]
    {:ratom/reset db}))

(defmethod base/handle :add-todo
  [_config _ {event :request/event db :ratom/db}]
  (let [id (allocate-next-id (:todos db))
        new-todo {:id id :title (:text event) :done false}
        new-db (assoc-in db [:todos id] new-todo)]
    {:ratom/reset new-db}))

(defmethod base/handle :toggle-done
  [_config _ {event :request/event db :ratom/db}]
  (let [{:keys [id]} event
        new-db (update-in db [:todos id :done] not)]
    {:ratom/reset new-db}))

(defmethod base/handle :save
  [_config _ {event :request/event db :ratom/db}]
  (let [{:keys [id title]} event
        new-db (assoc-in db [:todos id :title] title)]
    {:ratom/reset new-db}))

(defmethod base/handle :delete-todo
  [_config _ {event :request/event db :ratom/db}]
  (let [{:keys [id]} event
        new-db (update-in db [:todos] dissoc id)]
    {:ratom/reset new-db}))

(defmethod base/handle :clear-completed
  [_config _ {db :ratom/db}]
  (let [todos (:todos db)
        done-ids (->> (vals todos)
                      (filter :done)
                      (map :id))
        new-todos (reduce dissoc todos done-ids)
        new-db (assoc db :todos new-todos)]
    {:ratom/reset new-db}))

(defmethod base/handle :complete-all-toggle
  [_config _ {db :ratom/db}]
  (let [todos (:todos db)
        new-done (not-every? :done (vals todos))
        new-todos (reduce #(assoc-in %1 [%2 :done] new-done)
                          todos
                          (keys todos))
        new-db (assoc db :todos new-todos)]   ;; work out: toggle true or false?
    {:ratom/reset new-db}))


(comment

  (def tap (partial base/tap {}))
  (def handle (partial base/handle {:tap tap}))

  (def db (tap :add-todo {:text "abc"}))

  (handle :add-todo {:ratom/db db :event {:text "abc"}})
  (handle :save {:ratom/db db :event {:id 1 :title "hello"}})

  )
