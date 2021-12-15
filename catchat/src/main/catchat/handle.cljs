(ns catchat.handle
  (:require
   [fancoil.base :as base]
   [clojure.string :as str]
   [datascript.core :as d]
   [catchat.util :as u]))


(def ^:dynamic *room-msg-limit* 30)

;; helper functions 

(defn- user-stub [uid]
  {:db/id       uid
   :user/name   "Loading..."
   :user/avatar "web/avatars/loading.jpg"
   :user/state :loading})

(defn- select-room [db room-id]
  (let [selected (d/q '[:find ?r .
                        :where [?r :room/selected true]] db)]
    (case selected
      nil [[:db/add room-id :room/selected true]]
      room-id []
      [[:db/retract selected :room/selected true]
       [:db/add room-id :room/selected true]])))

(defn- mark-read [db room-id]
  (let [unread (d/q '[:find [?m ...]
                      :in $ ?r
                      :where [?m :message/unread]
                      [?m :message/room ?r]]
                    db room-id)]
    (map (fn [mid] [:db/retract mid :message/unread true]) unread)))


;; implement base/handle 

(defmethod base/handle :event/send-msg
  [_config _sig {db :ds/db event :request/event}]
  (when-not (str/blank? event)
    (let [msg {:message/room   (u/q1-by db :room/selected)
               :message/author (u/q1-by db :user/me)
               :message/text   event}]
      {:server/send msg})))


(defmethod base/handle :event/recv-msg
  [_config _sig {db :ds/db event :request/event}]
  (let [room (u/q1-by db :room/selected)
        msg  (if (== (:message/room event) room)
               (dissoc event :message/unread)
               event)
        load-user-req #:request{:signal :event/load-user
                                :event {:uid (:message/author msg)}}]
    {:ds/tx [msg]
     :dispatch/request load-user-req}))


(defmethod base/handle :event/load-user
  [_config _sig {db :ds/db event :request/event}]
  (let [{:keys [uid]} event
        user (d/q '[:find ?e .
                    :in $ ?e
                    :where [?e :user/state :loaded]]
                  db uid)]
    (when-not user
      {:ds/tx [(user-stub uid)]
       :dispatch/request #:request
                          {:signal :server/get-user
                           :event [uid]
                           :callback (fn [user]
                                       #:event {:action :tx/tx
                                                :detail [(assoc user :user/state :loaded)]})}})))


(defmethod base/handle :event/select-room
  [_config _sig {db :ds/db event :request/event}]
  (let [{:keys [room-id]} event]
    {:ds/tx [[:db.fn/call select-room room-id]
             [:db.fn/call mark-read room-id]]}))


(defmethod base/handle :event/clean-msg
  [_config _sig {db :ds/db event :request/event}]
  (let [msg event
        room-id     (:message/room msg)
        ;; Last ?lim messages
        keep-msgs   (->> (d/q '[:find (max ?lim ?m) .
                                :in $ ?room-id ?lim
                                :where [?m :message/room ?room-id]]
                              db
                              room-id
                              *room-msg-limit*)
                         set)
        ;; All other messages in same room
        remove-msgs (d/q '[:find [?m ...]
                           :in $ ?room-id ?remove-pred
                           :where [?m :message/room ?room-id]
                           [(?remove-pred ?m)]] ;; filter by custom predicate
                         db
                         room-id
                         #(not (contains? keep-msgs %)))]
    {:ds/tx (map #(vector :db.fn/retractEntity %) remove-msgs)}))


