(ns realworld.handle.page
  (:require
   [clojure.string :as string]
   [cljs.reader :as reader]
   [fancoil.base :as base]))


(defn add-epoch
  "Add :epoch timestamp based on :createdAt field."
  [item]
  (assoc item :epoch (-> item :createdAt reader/parse-timestamp .getTime)))

(defn index-by
  "Index collection by function f (usually a keyword) as a map"
  [f coll]
  (into {}
        (map (fn [item]
               (let [item (add-epoch item)]
                 [(f item) item])))
        coll))


;; -- GET Articles @ /api/articles --------------------------------------------

(defmethod base/handle :page/get-articles-response
  [_ _ {response :ajax/response route :router/route}]
  (let [{:keys [page-path]} route
        {articles :articles, articles-count :articlesCount} response
        articles-map (index-by :slug articles)]
    {:ratom/set-paths {[:pages page-path :articles] articles-map
                       [:pages page-path :articles-count] articles-count}}))


;; -- POST Article @ /api/articles ---------------------------------------
;; create new articles

(defmethod base/handle :page/create-article-response
  [_ _ {response :ajax/response}]
  (let [article (:article response)]
    {:dispatch/request #:request {:method :api/get-article
                                  :body {:slug (:slug article)
                                         :on-success :page/get-article-response}}
     :router/navigate {:page-name :page/article-page
                       :params {:slug (:slug article)}}}))


;; -- GET Article @ /api/articles/:slug ---------------------------------------
;; get a article

(defmethod base/handle :page/get-article-response
  [_ _  {response :ajax/response route :router/route}]
  (let [{article :article} response
        {page-path :page-path} route]
    {:ratom/set-paths {[:pages page-path :articles (:slug article)] article}
     :dispatch/request #:request {:method :api/get-profile
                                  :body {:username (get-in article [:author :username])
                                         :on-success :page/get-profile-response}}}))

;; -- PUT Article @ /api/articles/:slug --------------------------------
;; update article

(defmethod base/handle :page/update-article-response
  [_ _ {response :ajax/response}]
  (let [{:keys [article]} response]
    {:ratom/delete-paths [[:errors]]
     :dispatch/requests [#:request {:method :api/get-article
                                    :body {:slug (:slug article)
                                           :on-success :page/get-article-response}}
                         #:request {:method :api/get-comments
                                    :body {:slug (:slug article)
                                           :on-success :page/get-comments-response}}]
     :router/navigate {:page-name :page/article-page
                       :params {:slug (:slug article)}}}))


;; -- DELETE Article @ /api/articles/:slug ------------------------------------
;;

(defmethod base/handle :page/delete-article-response
  [_ _ {body :request/body}]
  {:ratom/delete-paths [[:articles (:slug body)]]
   :router/navigate {:page-name :page/home-page}})


;; -- GET Comments @ /api/articles/:slug/comments -----------------------------
;;

(defmethod base/handle :page/get-comments-response
  [_ _ {response :ajax/response {:keys [page-path path-params]} :router/route}]
  (let [{:keys [comments]} response
        {:keys [slug]} path-params]
    {:ratom/set-paths {[:pages page-path :articles slug :comments] (index-by :id comments)}}))


;; -- POST Comments @ /api/articles/:slug/comments ----------------------------

(defmethod base/handle :page/post-comment-response
  [_ _ {response :ajax/response {:keys [page-path path-params]} :router/route}]
  (let [{:keys [comment]} response
        slug (:slug path-params)]
    {:ratom/set-paths     {[:pages page-path :articles slug :comments (:id comment)] comment}
     :ratom/delete-paths [[:errors :comments]]
     :dispatch/request #:request {:method :api/get-comments
                                  :body {:slug slug
                                         :on-success :page/get-comments-response}}}))

(defmethod base/handle :page/delete-comment-response
  [_ _ {response :ajax/response {:keys [page-path path-params]} :router/route body :request/body}]
  (let [{:keys [comment-id]} body
        {:keys [slug]} path-params]
    {:ratom/delete-paths [[:pages page-path :articles slug :comments comment-id]]}))

;; --  favorite article @ /api/articles/:slug/favorite ------------------

(defmethod base/handle :page/favorite-article-response
  [_ _ {response :ajax/response {:keys [page-path]} :router/route db :ratom/db}]
  (let [{article :article} response
        {:keys [slug favorited favoritesCount]} article]
    {:ratom/set-paths {[:pages page-path :articles slug :favorited] favorited
                       [:pages page-path :articles slug :favoritesCount] favoritesCount}}))


;; --  favorite article @ /api/articles/:slug/favorite ------------------

(defmethod base/handle :page/change-new-comment
  [_ _ {body :request/body route :router/route}]
  (let [{:keys [slug comment]} body
        {:keys [page-path]} route]
    {:ratom/set-paths {[:pages page-path :articles slug :new-comment] comment}}))

(defmethod base/handle :page/delete-new-comment
  [_ _ {body :request/body route :router/route}]
  (let [{:keys [slug]} body
        {:keys [page-path]} route]
    {:ratom/delete-paths [[:pages page-path :articles slug :new-comment]]}))

;; -- GET Profile @ /api/profiles/:username -----------------------------------

(defmethod base/handle :page/get-profile-response
  [_ _ {db :ratom/db response :ajax/response route :router/route}]
  (let [{:keys [profile]} response
        {:keys [page-path]} route
        username (:username profile)]
    {:ratom/set-paths {[:pages page-path :profiles username] profile}}))


;; -- Follow user @ /api/profiles/:username/follow ---------------------

(defmethod base/handle :page/follow-status-response
  [_ _ {response :ajax/response {:keys [page-path]} :router/route}]
  (let [{profile :profile} response
        {:keys [username following]} profile]
    {:ratom/set-paths {[:pages page-path :profiles username :following] following}}))


;; tags
;; -- GET Tags @ /api/tags ----------------------------------------------------

(defmethod base/handle :page/get-tags-response
  [_ _ {response :ajax/response {:keys [page-path]} :router/route}]
  (let [{:keys [tags]} response]
    {:ratom/set-paths {[:pages page-path :tags] tags}}))

;; user
;; -- POST Login @ /api/users/login -------------------------------------------

(defmethod base/handle :page/login-response
  [_ _ {response :ajax/response}]
  (let [{:keys [user]} response]
    {:ratom/set-paths {[:user] user}
     :router/navigate {:page-name :page/home-page}}))

;; -- POST Registration @ /api/users ------------------------------------------

(defmethod base/handle :page/register-user-response
  [_ _ {response :ajax/response}]
  (let  [{:keys [user]} response]
    {:ratom/set-paths    {[:user] user}
     :router/navigate {:page-name :page/home-page}}))


;; -- PUT Update User @ /api/user ---------------------------------------------

(defmethod base/handle :page/update-user-response
  [_ _ {db :ratom/db response :ajax/response}]
  (let [{:keys [user]} response
        old-user (:user db)]
    {:ratom/set-paths {[:user] (merge old-user user)}}))


(defmethod base/handle :page/get-articles-request
  [_ _ {route :router/route body :request/body}]
  (let [{:keys [page-path]} route
        {:keys [tag feed]} (:params body)]
    [[:ratom/set-paths {[:pages page-path :filter] {:tag tag :feed feed :offset 0 :limit 10}}]
     [:dispatch/request (cond
                          feed  #:request{:method :api/get-feed
                                          :body {:params {:feed true :offset 0 :limit 100}
                                                 :on-success :page/get-articles-response}}
                          tag  #:request{:method :api/get-articles
                                         :body {:params {:tag tag :offset 0 :limit 100}
                                                :on-success :page/get-articles-response}}
                          :else #:request{:method :api/get-articles
                                          :body {:params {:offset 0 :limit 100}
                                                 :on-success :page/get-articles-response}})]]))

(defmethod base/handle :page/change-filter
  [_ _ {route :router/route body :request/body}]
  (let [{:keys [page-path]} route
        {:keys [limit offset]} body]
    [[:ratom/set-paths {[:pages page-path :filter :limit] limit
                        [:pages page-path :filter :offset] offset}]]))

(defmethod base/handle :page/set-cache-item
  [_ _ {body :request/body {:keys [page-path]} :router/route}]
  (let [{:keys [cache-item]} body
        [cache-path cache-value] cache-item]
    {:ratom/set-paths {(concat [:pages page-path] cache-path) cache-value}}))

;; specific pages



(defmethod base/handle :home-page/load
  [_ _ {db :ratom/db {:keys [page-path]} :router/route}]
  [[:ratom/set-paths {[:pages page-path :filter] {:offset 0 :limit 10}}]
   [:dispatch/request #:request{:method :api/get-tags
                                :body {:on-success :page/get-tags-response}}]
   [:dispatch/request #:request {:method :page/get-articles-request
                                 :body {:params {:offset 0 :limit 100 :feed (boolean (:user db))}
                                        :on-success :page/get-articles-response}}]])


(defmethod base/handle :article-page/load
  [_ _ {body :request/body}]
  (let [{:keys [slug]} (:path-params body)]
    {:dispatch/requests [#:request {:method :api/get-article
                                    :body {:slug slug
                                           :on-success :page/get-article-response}}
                         #:request {:method :api/get-comments
                                    :body {:slug slug
                                           :on-success :page/get-comments-response}}]}))

(defmethod base/handle :article-page/follow-status-response
  [_ _ {response :ajax/response {:keys [page-path path-params]} :router/route}]
  (let [{profile :profile} response
        {:keys [slug]} path-params
        {:keys [username following]} profile]
    {:ratom/set-paths {[:pages page-path :articles slug :author :following] following}}))

(defmethod base/handle :profile-page/load
  [_ _ {body :request/body route :router/route}]
  (let [{:keys [username]} (:path-params route)
        {:keys [page-path]} route]
    {:ratom/set-paths {[:pages page-path :filter] {:author username :offset 0 :limit 10}}
     :dispatch/requests [#:request {:method :api/get-profile
                                    :body {:username username
                                           :on-success :page/get-profile-response}}
                         #:request {:method :api/get-articles
                                    :body {:params {:author username :limit 100 :offset 0}
                                           :on-success :page/get-articles-response}}]}))

(defmethod base/handle :profile-page-favorites/load
  [_ _ {body :request/body route :router/route}]
  (let [{:keys [username]} (:path-params route)
        {:keys [page-path]} route]
    {:ratom/set-paths {[:pages page-path :filter] {:favorites username :offset 0 :limit 10}}
     :dispatch/requests [#:request {:method :api/get-profile
                                    :body {:username username
                                           :on-success :page/get-profile-response}}
                         #:request {:method :api/get-articles
                                    :body {:params {:favorited username :limit 100 :offset 0}
                                           :on-success :page/get-articles-response}}]}))

(defmethod base/handle :editor-article-page/load
  [_ _ {route :router/route}]
  (let [{:keys [path-params]} route
        {:keys [slug]} path-params]
    {:dispatch/requests [#:request {:method :api/get-article
                                    :body {:slug slug
                                           :on-success :editor-article-page/set-article-input}}]}))

(defmethod base/handle :editor-article-page/set-article-input
  [_ _  {response :ajax/response route :router/route}]
  (let [{article :article} response
        {page-path :page-path} route
        raw-taglist (string/join " " (:tagList article))]
    {:ratom/set-paths {[:pages page-path :article-input] (assoc article :tagList raw-taglist)}}))

(defmethod base/handle :login-page/logout
  [_ _ _]
  {:ratom/delete-paths [[:user]]
   :router/navigate {:page-name :page/home-page}})

;; router hook

(defmethod base/handle :router/on-navigate
  [_ _ {route :request/body}]
  (let [page-name (:page-name route)
        fx (case page-name
             :page/home-page {:dispatch/request #:request{:method :home-page/load :body route}}
             :page/article-page {:dispatch/request #:request{:method :article-page/load :body route}}
             :page/profile-page {:dispatch/request #:request{:method :profile-page/load :body route}}
             :page/profile-page-favorites {:dispatch/request #:request{:method :profile-page-favorites/load :body route}}
             :page/editor-article-page {:dispatch/request #:request{:method :editor-article-page/load :body route}}
             {:log/out (str "router/on-navigate:" route)})]
    (assoc fx :ratom/set-paths {[:current-route] route})))

