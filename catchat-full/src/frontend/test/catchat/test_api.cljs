(ns catchat.test-api
  (:require
   [cljs-http.client :as http]
   [cljs.core.async :refer [go <!]]))



(go 
  (println (<! (http/post "/api/get-rooms" {}))))