(ns catchat-server.mock.chat-rooms
  (:require
   [clojure.java.io :as io]
   [datascript.core :as d]))


(def fixtures
  [{:room/title "World domination"     :room/source "web/rooms/world_domination.edn"}
   {:room/title "Pussies"              :room/source "web/rooms/pussies.edn"}
   {:room/title "Internet of cats"     :room/source "web/rooms/cats.edn"}
   {:room/title "Paw care"             :room/source "web/rooms/paw_care.edn"}
   {:room/title "Puss in Boots sequel" :room/source "web/rooms/puss_in_boots.edn"}
   {:room/title "Afterlife"            :room/source "web/rooms/afterlife.edn"}
   {:user/name "Starry"             :user/avatar "web/avatars/a1.jpg"}
   {:user/name "Friar Tuck"         :user/avatar "web/avatars/a2.jpg"}
   {:user/name "Toom"               :user/avatar "web/avatars/a3.jpg"}
   {:user/name "Hansel"             :user/avatar "web/avatars/a4.jpg"}
   {:user/name "Cuddlebug"          :user/avatar "web/avatars/a5.jpg"}
   {:user/name "Georgie"            :user/avatar "web/avatars/a6.jpg"}
   {:user/name "Jean-Paul Gizmondo" :user/avatar "web/avatars/a7.jpg"}
   {:user/name "Gorgeous Furboy"    :user/avatar "web/avatars/a8.jpg"}
   {:user/name "Jiggle Belly"       :user/avatar "web/avatars/a9.jpg"}
   {:user/name "Invitation"         :user/avatar "web/avatars/a10.jpg"}
   {:user/name "The Phantom"        :user/avatar "web/avatars/a11.jpg"}
   {:user/name "Rupert"             :user/avatar "web/avatars/a12.jpg"}
   {:user/name "Obstinate"          :user/avatar "web/avatars/a13.jpg"}
   {:user/name "Bunter"             :user/avatar "web/avatars/a14.jpg"}
   {:user/name "Porsche"            :user/avatar "web/avatars/a15.jpg"}
   {:user/name "Puka"               :user/avatar "web/avatars/a16.jpg"}
   {:user/name "Tabba To"           :user/avatar "web/avatars/a17.jpg"}
   {:user/name "Artful Dodger"      :user/avatar "web/avatars/a18.jpg"}
   {:user/name "Half Hot Chocolate" :user/avatar "web/avatars/a19.jpg"}
   {:user/name "Budmeister"         :user/avatar "web/avatars/a20.jpg"}
   {:user/name "Scsi2"              :user/avatar "web/avatars/a21.jpg"}
   {:user/name "BigMouth"           :user/avatar "web/avatars/a22.jpg"}
   {:user/name "Splinter"           :user/avatar "web/avatars/a23.jpg"}
   {:user/name "Isidor"             :user/avatar "web/avatars/a24.jpg"}
   {:user/name "Chanel"             :user/avatar "web/avatars/a25.jpg"}])



;; load all room messages variants

(defn load-rooms-and-users!
  [conn]
  (d/transact! conn fixtures))

(defn load-messages! [conn]
  (doseq [[id url title] (d/q '[:find ?id ?src ?title
                                :where [?id :room/source ?src]
                                [?id :room/title ?title]] @conn)]
    (let [messages (read-string (slurp (io/resource (str "catchat_server/public/" url))))]
      (d/transact! conn [{:db/id id
                          :room/messages messages}]))))

(defn create-mock-conn! []
  (let [conn (d/create-conn {:room/messages {:db/cardinality :db.cardinality/many}})]
    (load-rooms-and-users! conn)
    (load-messages! conn)
    conn))

(defonce conn 
  (create-mock-conn!))


;; GENERATORS

(defn- rand-user-id [db]
  (d/q '[:find  (rand ?id) .
         :where [?id :user/name]]
       db))

(defn- rand-room [db]
  (d/q '[:find  (rand ?id) .
         :where [?id :room/title]]
       db))

(defn- rand-message [db room-id]
  (-> (d/datoms db :aevt :room/messages room-id) (rand-nth) :v))

;; HELPERS

(defn- user-by-id [db id]
  (-> (d/entity db id)
      (select-keys [:db/id :user/name :user/avatar])))

;; "REST" API


(defn get-rooms
  "Return list of rooms"
  []
  (->> @conn
       (d/q '[:find ?id ?title
              :where [?id :room/title ?title]])
       (mapv #(zipmap [:db/id :room/title] %))))


(defn get-user
  "Return specific user entity"
  [id]
  (user-by-id @conn id))

(defn whoami
  "Return current user entity"
  []
  (let [db @conn
        id (rand-user-id db)]
    (user-by-id db id)))


;; MESSAGING

(def ^:private next-msg-id (atom 10000))

(defn generate-new-message []
  (let [db @conn
        room-id   (rand-room db)
        text      (rand-message db room-id)
        author-id (rand-user-id db)
        msg     {:db/id             (swap! next-msg-id inc)
                 :message/text      text
                 :message/author    author-id
                 :message/room      room-id
                 :message/unread    true
                 :message/timestamp (java.util.Date.)}]
    msg))


(defn insert-new-message [message]
  (let [new-message (assoc message
                           :db/id (swap! next-msg-id inc)
                           :message/timestamp (java.util.Date.))]
    new-message))
