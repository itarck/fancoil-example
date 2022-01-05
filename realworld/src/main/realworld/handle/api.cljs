(ns realworld.handle.api
  (:require
   [clojure.string :as string :refer [trim split join]]
   [fancoil.base :as base]
   [fancoil.module.cljs-ajax.plugin]))


;; (def api-url "http://localhost:6003/api")

(def api-url "https://api.realworld.io/api")


(defn endpoint
  "Concat any params to api-url separated by /"
  [& params]
  (string/join "/" (cons api-url params)))

(defn auth-header
  "Get user token and format for API authorization"
  [db]
  (when-let [token (get-in db [:user :token])]
    [:Authorization (str "Token " token)]))

(defn gen-opt [db]
  {:headers         (auth-header db)       ;; get and pass user token obtained during login
   :format          :json
   :response-format :json
   :keywords? true})


(defn api-error-request [request-type]
  #:request {:method :api/request-error
             :body {:request-type request-type}})

(defn reset-loading-request [api-type]
  #:request {:method :do/effect
             :body {:ratom/set-paths {[:loading api-type] false}}})


;; -- GET Articles @ /api/articles --------------------------------------------
;; Get all articles

(defmethod base/handle :api/get-articles
  [_ method {body :request/body db :ratom/db }]
  (let [{:keys [on-success params]} body]
    ;; (println "api/get-articles " params)
    [[:ratom/set-paths {[:loading :articles] true}]
     [:ajax/get {:uri (endpoint "articles")
                 :opt (assoc (gen-opt db) :params params)
                 :on-success on-success
                 :on-failure (api-error-request method)
                 :finally (reset-loading-request :articles)}]]))


;; -- POST Article @ /api/articles ---------------------------------------
;; create new articles

(defn format-article [content]
  {:title       (trim (or (:title content) ""))
   :description (trim (or (:description content) ""))
   :body        (trim (or (:body content) ""))
   :tagList     (split (:tagList content) #" ")})


(defmethod base/handle :api/create-article
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [article-input on-success]} body
        article (format-article article-input)]
    [[:ratom/set-paths {[:loading :article] true}]
     [:ajax/post {:uri (endpoint "articles")
                  :opt (assoc (gen-opt db)
                              :params {:article article})
                  :on-success on-success
                  :on-failure (api-error-request method)
                  :finally (reset-loading-request :article)}]]))

;; -- GET Article @ /api/articles/:slug ---------------------------------------
;; get a article

(defmethod base/handle :api/get-article
  [_ method {body :request/body db :ratom/db}]                        ;; params = {:slug "slug"}
  (let [{:keys [on-success slug]} body]
    [[:ratom/reset (assoc-in db [:loading :article] true)]
     [:ajax/get {:uri  (endpoint "articles" slug)
                 :opt (gen-opt db)
                 :on-success on-success
                 :on-failure (api-error-request method)
                 :finally (reset-loading-request :article)}]]))


;; -- PUT Article @ /api/articles/:slug --------------------------------
;; update article

(defmethod base/handle :api/update-article
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [slug article-input on-success]} body
        article (format-article article-input)]
    [[:ratom/reset         (assoc-in db [:loading :article] true)]
     [:ajax/put {:uri         (endpoint "articles" slug)
                 :opt         (assoc (gen-opt db)
                                     :params {:article article})
                 :on-success  on-success
                 :on-failure (api-error-request method)
                 :finally (reset-loading-request :article)}]]))

;; -- DELETE Article @ /api/articles/:slug ------------------------------------

(defmethod base/handle :api/delete-article
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [slug on-success]} body]
    {:ratom/reset (assoc-in db [:loading :article] true)
     :ajax/delete {:uri    (endpoint "articles" slug)
                   :opt    (assoc (gen-opt db) :params slug)
                   :on-success    on-success
                   :on-failure    (api-error-request method)
                   :finally (reset-loading-request :article)}}))

;; -- GET Feed Articles @ /api/articles/feed ----------------------------------

(defmethod base/handle :api/get-feed
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [params on-success]} body]
    [[:ratom/set-paths {[:loading :articles] true}]
     [:ajax/get {:uri             (endpoint "articles" "feed") ;; evaluates to "api/articles/feed"
                 :opt (assoc (gen-opt db)
                             :params params)
                 :on-success      on-success
                 :on-failure      (api-error-request method)
                 :finally (reset-loading-request :articles)}]]))

;; -- GET Comments @ /api/articles/:slug/comments -----------------------------

(defmethod base/handle :api/get-comments
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [slug on-success]} body]
    {:ratom/set-paths    {[:loading :comments] true}
     :ajax/get {:uri             (endpoint "articles" slug "comments") ;; evaluates to "api/articles/:slug/comments"
                :opt             (gen-opt db)
                :on-success      on-success
                :on-failure     (api-error-request method)
                :finally (reset-loading-request :comments)}}))


;; -- POST Comments @ /api/articles/:slug/comments ----------------------------

(defmethod base/handle :api/post-comment
  [_ method {body :request/body db :ratom/db }]
  (let [{:keys [slug comment on-success]} body]
    {:ratom/set-paths       {[:loading :comments] true}
     :ajax/post {:uri             (endpoint "articles" slug "comments") ;; evaluates to "api/articles/:slug/comments"
                 :opt             (assoc (gen-opt db)
                                         :params {:comment comment})
                 :on-success      on-success
                 :on-failure      (api-error-request method)
                 :finally (reset-loading-request :comments)}}))

;; -- DELETE Comments @ /api/articles/:slug/comments/:comment-id ----------------------

(defmethod base/handle :api/delete-comment
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [comment-id slug on-success]} body]
    {:ratom/set-paths {[:loading :comments] true}
     :ajax/delete {:uri             (endpoint "articles" slug "comments" comment-id) ;; evaluates to "api/articles/:slug/comments/:comment-id"
                   :opt {:headers         (auth-header db)
                         :format          :json}
                   :on-success      on-success
                   :on-failure      (api-error-request method)
                   :finally (reset-loading-request :comments)}}))

;; --  favorite article @ /api/articles/:slug/favorite ------------------
;;

(defmethod base/handle :api/favorite-article
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [slug on-success]} body]                               ;; slug = :slug
    {:ratom/set-paths         {[:loading :favorite-article] true}
     :ajax/post {:uri          (endpoint "articles" slug "favorite") ;; evaluates to "api/articles/:slug/favorite"
                 :opt         (gen-opt db)
                 :on-success      on-success
                 :on-failure      (api-error-request method)
                 :finally (reset-loading-request :favorite-article)}}))


;; --  favorite article @ /api/articles/:slug/favorite ------------------

(defmethod base/handle :api/unfavorite-article
  [_ method {body :request/body db :ratom/db}]
  (let [{:keys [slug on-success]} body]                               ;; slug = :slug
    {:ratom/set-paths         {[:loading :unfavorite-article] true}
     :ajax/delete {:uri          (endpoint "articles" slug "favorite") ;; evaluates to "api/articles/:slug/favorite"
                   :opt         (gen-opt db)
                   :on-success      on-success
                   :on-failure      (api-error-request method)
                   :finally (reset-loading-request :unfavorite-article)}}))

;; -- GET Profile @ /api/profiles/:username -----------------------------------

(defmethod base/handle :api/get-profile
  [_ method {db :ratom/db body :request/body}]
  (let [{:keys [username on-success]} body]
    {:ratom/set-paths {[:loading :profile] true}
     :ajax/get {:uri (endpoint "profiles" username)
                :opt (gen-opt db)
                :on-success on-success
                :on-failure (api-error-request method)
                :finally (reset-loading-request :profile)}}))

;; -- Follow user @ /api/profiles/:username/follow ---------------------

(defmethod base/handle :api/follow
  [_ method {db :ratom/db body :request/body}]
  (let [{:keys [username on-success]} body]
    {:ratom/set-paths         {[:loading :toggle-following] true}
     :ajax/post {:uri             (endpoint "profiles" username "follow") ;; evaluates to "api/profiles/:username/follow"
                 :opt (gen-opt db)
                 :on-success      on-success
                 :on-failure      (api-error-request method)
                 :finally (reset-loading-request :toggle-following)}}))


;; -- Unfollow user @ /api/profiles/:username/follow ---------------------

(defmethod base/handle :api/unfollow
  [_ method {db :ratom/db body :request/body}]
  (let [{:keys [username on-success]} body]
    {:ratom/set-paths         {[:loading :toggle-following] true}
     :ajax/delete {:uri             (endpoint "profiles" username "follow") ;; evaluates to "api/profiles/:username/follow"
                   :opt (gen-opt db)
                   :on-success      on-success
                   :on-failure      (api-error-request method)
                   :finally (reset-loading-request :toggle-following)}}))


;; -- GET Tags @ /api/tags ----------------------------------------------------

(defmethod base/handle :api/get-tags
  [_ _ {db :ratom/db body :request/body}]
  (let [{:keys [on-success]} body]
    {:ratom/set-paths {[:loading :tags] true}
     :ajax/get {:uri (endpoint "tags")      ;; evaluates to "api/tags"
                :opt (gen-opt db)
                :on-success on-success
                :finally (reset-loading-request :tags)}}))

;; user
;; -- POST Login @ /api/users/login -------------------------------------------

(defmethod base/handle :api/login
  [_ _ {body :request/body db :ratom/db}]
  (let [{:keys [user on-success]} body]
    {:ratom/set-paths        {[:loading :login] true}
     :ajax/post {:uri             (endpoint "users" "login") ;; evaluates to "api/users/login"
                 :opt (assoc (gen-opt db) :params {:user user})
                 :on-success      on-success
                 :finally (reset-loading-request :login)}}))


;; -- POST Registration @ /api/users ------------------------------------------
;;
(defmethod base/handle :api/register-user
  [_ method {db :ratom/db body :request/body}]
  (let [{:keys [user on-success]} body]                       ;; registration = {:username ... :email ... :password ...}
    {:ratom/set-paths         {[:loading :register-user] true}
     :ajax/post {:uri             (endpoint "users")     ;; evaluates to "api/users"
                 :opt             (assoc (gen-opt db)
                                         :params {:user user})
                 :on-success      on-success
                 :on-failure      (api-error-request method)
                 :finally (reset-loading-request :register-user)}}))

;; -- PUT Update User @ /api/user ---------------------------------------------

(defmethod base/handle :api/update-user
  [_ method {db :ratom/db body :request/body}]
  (let [{:keys [user on-success]} body]
    {:ratom/set-paths    {[:loading :update-user] true}
     :ajax/put {:uri             (endpoint "user")      ;; evaluates to "api/user"
                :opt             (assoc (gen-opt db) :params {:user user})
                :on-success      on-success
                :on-failure      (api-error-request method)
                :finally (reset-loading-request :update-user)}}))


(defmethod base/handle :api/request-error
  [_ _ {body :request/body
        response :ajax/response}]
  (let [{:keys [request-type]} body]
    {:ratom/set-paths {[:errors request-type] (get-in response [:response :errors])}
     :log/error response}))