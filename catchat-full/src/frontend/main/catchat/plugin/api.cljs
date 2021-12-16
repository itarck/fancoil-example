(ns catchat.plugin.api
  (:require
   [cljs-http.client :as http]
   [cljs.reader :refer [read-string]]
   [cljs.core.async :refer [go go-loop >! <! chan] :as async]
   [fancoil.base :as base]))


(defmethod base/do! :api/post
  [config _ request]
  (go
    (let [{:keys [uri body callback] :or {body {}}} request
          response (<! (http/post uri body))]
      (if (= (:status response) 200)
        (let [event (read-string (:body response))]
          (println "post callback" event)
          (cond
            (fn? callback) (callback event)
            (keyword? callback) (let [req #:request {:signal callback
                                                     :event event}]
                                  (base/do! config :dispatch/request req))))
        (println "error" response)))))

(defmethod base/do! :api/get
  [config _ request]
  (go
    (let [{:keys [uri body callback] :or {body {}}} request
          response (<! (http/get uri body))]
      (if (= (:status response) 200)
        (let [event (read-string (:body response))]
          (cond
            (fn? callback) (callback event)
            (keyword? callback) (let [req #:request {:signal callback
                                                     :event event}]
                                  (base/do! config :dispatch/request req))))
        (println "error" response)))))