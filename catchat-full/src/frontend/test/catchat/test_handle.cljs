(ns catchat.test-handle
  (:require 
   [datascript.core :as d ]
   [fancoil.base :as base]
   [catchat.db :as db]
   [catchat.handle]))

(def conn
  (d/create-conn db/schema))


(def handle
  (partial base/handle {:ds/db @conn}))

(handle :event/send-msg 
        #:request {:event "abc"})

(handle :event/load-user
        #:request {:event {:uid 100}})