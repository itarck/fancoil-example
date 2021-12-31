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
  [_config _sig {db :ds/db body :request/body}]
  (when-not (str/blank? body)
    (let [msg {:message/room   (u/q1-by db :room/selected)
               :message/author (u/q1-by db :user/me)
               :message/text   body}]
      {:chat-session/send! msg})))


(defmethod base/handle :event/recv-msg
  [_config _sig {db :ds/db body :request/body}]
  (let [room (u/q1-by db :room/selected)
        msg  (if (== (:message/room body) room)
               (dissoc body :message/unread)
               body)
        load-user-req #:request{:method :event/load-user
                                :body {:uid (:message/author msg)}}]
    {:ds/tx [msg]
     :dispatch/request load-user-req}))


(defmethod base/handle :event/load-user
  [_config _sig {db :ds/db body :request/body}]
  (let [{:keys [uid]} body
        user (d/q '[:find ?e .
                    :in $ ?e
                    :where [?e :user/state :loaded]]
                  db uid)]
    (when-not user
      {:ds/tx [(user-stub uid)]
      ;;  :ajax/post {:uri "/api/get-user"
      ;;              :opt {:params {:id uid}}
      ;;              :on-success :user/save}
       :ajax/request {:request {:method :post
                                :uri "/api/get-user"
                                :params {:id uid}}
                      :on-success :user/save}
       })))

(defmethod base/handle :user/save
  [_config _sig {db :ds/db user :ajax/response}]
  {:ds/tx [(assoc user :user/state :loaded)]})

(defmethod base/handle :event/select-room
  [_config _sig {db :ds/db body :request/body}]
  (let [{:keys [room-id]} body]
    {:ds/tx [[:db.fn/call select-room room-id]
             [:db.fn/call mark-read room-id]]}))


(defmethod base/handle :event/clean-msg
  [_config _sig {db :ds/db body :request/body}]
  (let [msg body
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


(defmethod base/handle :room/get-rooms
  [_config _sig _req]
  {:ajax/post {:uri "/api/get-rooms"
               :on-success :room/get-rooms-callback}})

(defmethod base/handle :room/get-rooms-callback
  [_config _sig {rooms :ajax/response}]
  [[:ds/tx rooms]
   [:dispatch/request #:request
                       {:method :event/select-room
                        :body {:room-id (:db/id (first rooms))}}]])

(defmethod base/handle :user/load-whoami
  [_config _sig _req]
  {:ajax/post {:uri "/api/whoami"
              :on-success :user/load-whomi-callback}})

(defmethod base/handle :user/load-whomi-callback
  [_config _sig {user :ajax/response}]
  (let [user (assoc user
                    :user/me true
                    :user/state :loaded)]
    {:ds/tx [user]}))

