(ns catchat.module.ajax
  (:require
   [cljs.core.async :refer [go]]
   [fancoil.base :as base]
   [ajax.core :refer [json-request-format json-response-format GET POST]]
   [ajax.simple :refer [ajax-request]]
   [clojure.string :as str]))


(defmethod base/do! :ajax/get
  [config _ effect]
  (go
    (let [{:keys [uri opt callback] :or {opt {}}} effect
          opt (assoc opt :handler
                     (fn [response]
                       (let [req #:request {:method callback
                                            :event response}]
                         (base/do! config :dispatch/request req))))]
      (GET uri opt))))

(defmethod base/do! :ajax/post
  [config _ effect]
  (go
    (let [{:keys [uri opt callback] :or {opt {}}} effect
          opt (assoc opt :handler
                     (fn [response]
                       (let [req #:request {:method callback
                                            :event response}]
                         (base/do! config :dispatch/request req))))]
      (POST uri opt))))


(def default-request
  {:format          (json-request-format)
   :response-format (json-response-format {:keywords? true})})


(defmethod base/do! :ajax/request
  [config _ effect]
  (let [{:keys [request callback]} effect
        callback-fn (fn [[ok response]]
                      (if ok
                        (let [req #:request {:method callback
                                             :event response}]
                          (base/do! config :dispatch/request req))
                        (js/console.error (str response))))
        merged-request (->
                        (merge default-request request)
                        (assoc :handler callback-fn))]
    (ajax-request merged-request)))


(comment

  (def api-url "http://localhost:6003/api")

  (defn endpoint
    "Concat any params to api-url separated by /"
    [& params]
    (str/join "/" (cons api-url params)))


  (base/do! {} :ajax/get {:uri (endpoint "articles")
                          :opt {:keywords? true}
                          :callback :log/out})

  (base/do! {} :ajax/post {:uri (endpoint "users")
                           :opt {:params {:user {:username "Jacob6"
                                                 :email "jake6@gmail.com"
                                                 :password "jakejake"}}
                                 :format :json
                                 :handler #(prn %)}
                           :callback :log/out})

  (base/do! {} :ajax/request {:request {:method          :post
                                        :uri             (endpoint "users")     ;; evaluates to "api/users"
                                        :params          {:user {:username "Jacob8"
                                                                 :email "jake8@gmail.com"
                                                                 :password "jakejake"}}   ;; {:user {:username ... :email ... :password ...}}
                                        :format          (json-request-format)  ;; make sure it's json
                                        :response-format (json-response-format {:keywords? true}) ;; json response and all keys to keywords
                                        }
                              :callback :log/out})
  )