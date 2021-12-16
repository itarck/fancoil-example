(ns catchat-server.main
  (:gen-class)
  (:require
   [duct.core :as duct]
   [catchat-server.handler.api]
   [catchat-server.process]))

(duct/load-hierarchy)

(defn -main [& args]
  (let [keys     (or (duct/parse-keys args) [:duct/daemon])
        profiles [:duct.profile/prod]]
    (-> (duct/resource "catchat_server/config.edn")
        (duct/read-config)
        (duct/exec-config profiles keys))
    (System/exit 0)))
