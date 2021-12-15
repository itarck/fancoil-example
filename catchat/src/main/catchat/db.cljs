(ns catchat.db)


(def schema
  {:room/title     {}
   :message/room   {:db/valueType :db.type/ref}
   :message/text   {}
   :message/author {:db/valueType :db.type/ref}
   :message/timestamp {}
   :message/unread {}
   :user/name      {}
   :user/avatar    {}
   :user/me        {}
   :user/state     {}})
