(ns todomvc-ratom.plugin.local-storage
  (:require
   [cljs.reader]
   [fancoil.base :as base]))


;; -- Local Storage  ----------------------------------------------------------


(defmethod base/inject :local-storage/load-entity
  [env _fn {:keys [local-storage-key]} req]
  (let [entity (into (hash-map)
                     (some->> (.getItem js/localStorage local-storage-key)
                              (cljs.reader/read-string)))]
    (assoc-in req [:local-storage/entity local-storage-key] entity)))


(defmethod base/do! :local-storage/save-entity
  [env _fn {:keys [local-storage-key entity]}]
  (.setItem js/localStorage local-storage-key (str entity)))


(comment

  (def env {})

  (base/do! env :local-storage/save-entity {:local-storage-key "mykey"
                                            :entity {:hello "world"}})

  (base/inject env :local-storage/load-entity {:local-storage-key "mykey"} {}))