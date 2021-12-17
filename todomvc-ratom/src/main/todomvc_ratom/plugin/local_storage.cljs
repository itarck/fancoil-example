(ns todomvc-ratom.plugin.local-storage
  (:require
   [cljs.reader]
   [fancoil.base :as base]))


;; -- Local Storage  ----------------------------------------------------------


(defmethod base/inject :local-storage/load-entity
  [_config _fn {:keys [local-storage-key]} req]
  (let [entity (into (hash-map)
                     (some->> (.getItem js/localStorage local-storage-key)
                              (cljs.reader/read-string)))]
    (assoc-in req [:local-storage/entity local-storage-key] entity)))


(defmethod base/do! :local-storage/save-entity
  [_config _fn {:keys [local-storage-key entity]}]
  (.setItem js/localStorage local-storage-key (str entity)))


(comment

  (def config {})

  (base/do! config :local-storage/save-entity {:local-storage-key "mykey"
                                            :entity {:hello "world"}})

  (base/inject config :local-storage/load-entity {:local-storage-key "mykey"} {}))