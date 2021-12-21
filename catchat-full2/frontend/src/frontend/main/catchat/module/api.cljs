(ns catchat.module.api
  (:require
   [cljs-http.client :as http]
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go >! <!] :as async]
   [fancoil.base :as base]))


(defmethod base/do! :api/post
  [config _ request]
  (go
    (let [{:keys [uri body callback] :or {body {}}} request
          response (<! (http/post uri body))]
      (if (= (:status response) 200)
        (let [event (read-string (:body response))
              req #:request {:signal callback
                             :event event}]
          (base/do! config :dispatch/request req))
        (println "error" response)))))


(defmethod base/do! :api/get
  [config _ request]
  (go
    (let [{:keys [uri body callback] :or {body {}}} request
          response (<! (http/get uri body))]
      (if (= (:status response) 200)
        (let [event (read-string (:body response))
              req #:request {:signal callback
                             :event event}]
          (base/do! config :dispatch/request req))
        (println "error" response)))))
