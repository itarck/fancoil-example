(ns catchat-server.handler.http-api
  (:require
   [compojure.core :refer [context POST GET]]
   [integrant.core :as ig]
   [ring.util.response :refer [response]]
   [ring.middleware.format-params :refer [wrap-restful-params]]
   [ring.middleware.format-response :refer [wrap-restful-response]]
   [catchat-server.mock-db :as mock-db]))


(defmethod ig/init-key :catchat-server.handler/http-api
  [_ _config]
  (-> (context "/api" []
        (POST "/get-rooms" []
          (response (mock-db/get-rooms)))
        (POST "/whoami" []
          (response (mock-db/whoami)))
        (POST "/get-user" [id]
          (response (mock-db/get-user id))))
      (wrap-restful-params)
      (wrap-restful-response)))


