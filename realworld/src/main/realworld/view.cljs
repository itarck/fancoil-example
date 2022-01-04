(ns realworld.view
  (:require
   [fancoil.base :as base]))


;; -- Helpers -----------------------------------------------------------------
;;
(defn format-date
  [date]
  (.toDateString (js/Date. date)))


;; -- Tag Components -----------------------------------------------------------------

(defmethod base/view :tags/tags-list
  [_ _ tags-list]
  [:ul.tag-list
   (for [tag tags-list]
     [:li.tag-default.tag-pill.tag-outline {:key tag} tag])])

;; -- Error Components -----------------------------------------------------------------

(defmethod base/view :errors/errors-list
  [_core _ errors]
  [:ul.error-messages
   (for [[k [v]] errors]
     ^{:key k} [:li (str (name k) " " v)])])

;; -- Article components -----------------------------------------------------------------

(defmethod base/view :articles/article-meta
  [{:keys [subscribe dispatch router]} _ article]
  (let [{:keys [author createdAt favoritesCount favorited slug] :or {slug "" author {:username ""}}} article
        {:keys [following username]} author
        loading @(subscribe :app/loading)
        user @(subscribe :app/user)]
    [:div.article-meta
     [:a {:href (router :path-for :page/profile-page {:username username})}
      [:img {:src (:image author) :alt "user image"}] " "]
     [:div.info
      [:a.author {:href (router :path-for :page/profile-page {:username username})} username]
      [:span.date (format-date createdAt)]]
     (if (= (:username user) username)
       [:span
        [:a.btn.btn-sm.btn-outline-secondary {:href (router :path-for :page/editor-article-page {:slug slug})}
         [:i.ion-edit]
         [:span " Edit Article "]]
        " "
        [:a.btn.btn-outline-danger.btn-sm {:href     (router :path-for :page/home-page)
                                           :on-click #(dispatch :api/delete-article {:slug slug
                                                                                     :on-success #:request {:method :page/delete-article-response
                                                                                                            :body {:slug slug}}})}
         [:i.ion-trash-a]
         [:span " Delete Article "]]]
       (when (seq user)
         [:span
          [:button.btn.btn-sm.action-btn.btn-outline-secondary {:on-click (if following
                                                                            #(dispatch :api/unfollow {:username username
                                                                                                      :on-success :article-page/follow-status-response})
                                                                            #(dispatch :api/follow {:username username
                                                                                                    :on-success :article-page/follow-status-response}))
                                                                :class    (when (:toggle-following loading) "disabled")}
           [:i {:class (if following "ion-minus-round" "ion-plus-round")}]
           [:span (if following (str " Unfollow " username) (str " Follow " username))]]
          " "
          [:button.btn.btn-sm.btn-primary {:on-click (if favorited
                                                       #(dispatch :api/unfavorite-article {:slug slug
                                                                                           :on-success :page/favorite-article-response})
                                                       #(dispatch :api/favorite-article {:slug slug
                                                                                         :on-success :page/favorite-article-response}))
                                           :class    (cond
                                                       (not favorited) "btn-outline-primary"
                                                       (:favorite-article loading) "disabled")}
           [:i.ion-heart]
           [:span (if favorited " Unfavorite Post " " Favorite Post ")]
           [:span.counter "(" favoritesCount ")"]]]))]))


(defmethod base/view :articles/article-preview
  [{:keys [subscribe dispatch router] :as core} _ article]
  (let [{:keys [description slug createdAt title author favoritesCount favorited tagList] :or {slug "" author {:username ""}}} article
        loading @(subscribe :app/loading)
        user @(subscribe :app/user)
        authorname (:username author)]
    [:div.article-preview
     [:div.article-meta
      [:a {:href (router :path-for :page/profile-page {:username authorname})}
       [:img {:src (:image author) :alt "user image"}]]
      [:div.info
       [:a.author {:href (router :path-for :page/profile-page {:user-id authorname})} authorname]
       [:span.date (format-date createdAt)]]
      (when (seq user)
        [:button.btn.btn-primary.btn-sm.pull-xs-right {:on-click (if favorited
                                                                   #(dispatch :api/unfavorite-article {:slug slug
                                                                                                       :article article
                                                                                                       :on-success :page/favorite-article-response})
                                                                   #(dispatch :api/favorite-article {:slug slug
                                                                                                     :article article
                                                                                                     :on-success :page/favorite-article-response}))
                                                       :class    (cond
                                                                   (not favorited) "btn-outline-primary"
                                                                   (:favorite-article loading) "disabled")}
         [:i.ion-heart " "]
         [:span favoritesCount]])]
     [:a.preview-link {:href (router :path-for :page/article-page {:slug slug})}
      [:h1 title]
      [:p description]
      [:span "Read more ..."]
      [base/view core :tags/tags-list tagList]]]))


(defmethod base/view :articles/articles-list
  [core _ {:keys [articles loading-articles]}]
  [:div
   (if loading-articles
     [:div.article-preview
      [:p "Loading articles ..."]]
     (if (empty? articles)
       [:div.article-preview
        [:p "No articles are here... yet."]]
       (for [article articles]
         ^{:key (:slug article)}
         [base/view core :articles/article-preview article])))])

;; -- Common components -----------------------------------------------------------------

(defmethod base/view :common/header
  [{:keys [subscribe router]} _ {:keys [page-name]}]
  (let [user @(subscribe :app/user)]
    [:nav.navbar.navbar-light
     [:div.container
      [:a.navbar-brand {:href (router :path-for :page/home-page)} "conduit"]
      (if (empty? user)
        [:ul.nav.navbar-nav.pull-xs-right
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/home-page) :class (when (= page-name :page/home-page) "active")} "Home"]]
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/login-page) :class (when (= page-name :page/login-page) "active")} "Sign in"]]
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/register-page) :class (when (= page-name :page/register-page) "active")} "Sign up"]]]
        [:ul.nav.navbar-nav.pull-xs-right
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/home-page) :class (when (= page-name :page/home-page) "active")} "Home"]]
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/editor-new-page {:slug "new"}) :class (when (= page-name :page/editor-new-page) "active")}
           [:i.ion-compose "New Article"]]]
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/settings-page) :class (when (= page-name :page/settings-page) "active")}
           [:i.ion-gear-a "Settings"]]]
         [:li.nav-item
          [:a.nav-link {:href (router :path-for :page/profile-page {:user-id (:username user)}) :class (when (= page-name :page/profile-page) "active")} (:username user)
           [:img.user-pic {:src (:image user) :alt "user image"}]]]])]]))


(defmethod base/view :common/footer
  [{:keys [router]} _ _]
  [:footer
   [:div.container
    [:a.logo-font {:href (router :path-for :page/home-page)} "conduit"]
    [:span.attribution
     "An interactive learning project from "
     [:a {:href "https://thinkster.io"} "Thinkster"]
     ". Code & design licensed under MIT."]]])


;; -- Pages components -----------------------------------------------------------------

;; - home-page

(defmethod base/view :page/home-page
  [{:keys [subscribe dispatch router] :as core} _ route]
  (let [{:keys [page-path]} route
        page-cache @(subscribe :page/sub-cache {:page-path page-path})
        {:keys [filter articles-count tags] all-articles :articles} page-cache
        articles @(subscribe :page/sub-articles {:page-path page-path :offset (:offset filter) :limit (:limit filter)})
        loading @(subscribe :app/loading)
        user @(subscribe :app/user)
        get-articles (fn [event params]
                       (.preventDefault event)
                       (dispatch :page/get-articles-request {:params params}))]
    [:div.home-page
     (when (empty? user)
       [:div.banner
        [:div.container
         [:h1.logo-font "conduit"]
         [:p "A place to share your knowledge."]]])
     [:div.container.page
      [:div.row
       [:div.col-md-9
        [:div.feed-toggle
         [:ul.nav.nav-pills.outline-active
          (when (seq user)
            [:li.nav-item
             [:a.nav-link {:href     (router :path-for :page/home-page)
                           :class    (when (:feed filter) "active")
                           :on-click #(get-articles % {:feed true})} "Your Feed"]])
          [:li.nav-item
           [:a.nav-link {:href     (router :path-for :page/home-page)
                         :class    (when-not (or (:tag filter) (:feed filter)) "active")
                         :on-click #(get-articles % {:feed false})} "Global Feed"]]
          (when (:tag filter)
            [:li.nav-item
             [:a.nav-link.active
              [:i.ion-pound] (str " " (:tag filter))]])]]
        [base/view core :articles/articles-list {:articles articles
                                                :loading-articles (:articles loading)}]
        (when-not (or (:articles loading) (< articles-count 10))
          [:ul.pagination
           (for [page (range (/ articles-count 10))]
             ^{:key page} [:li.page-item {:class    (when (= (* page 10) (:offset filter)) "active")
                                            :on-click #(dispatch :page/change-filter {:offset (* page 10)
                                                                                      :limit  10})}
                             [:a.page-link {:href (router :path-for :page/home-page)} (inc page)]])])]
       [:div.col-md-3
        [:div.sidebar
         [:p "Popular Tags"]
         (if (:tags loading)
           [:p "Loading tags ..."]
           [:div.tag-list
            (for [tag tags]
              ^{:key tag} [:a.tag-pill.tag-default {:href     (router :path-for :page/home-page)
                                                    :on-click #(get-articles % {:tag    tag})}
                           tag])])]]]]]))

;; - article-page

(defmethod base/view :page/article-page
  [{:keys [dispatch subscribe router] :as core} _ route]
  (let [{{:keys [slug]} :path-params page-path :page-path} route
        page-cache @(subscribe :page/sub-cache {:page-path page-path})
        article (get-in page-cache [:articles slug])
        new-comment (:new-comment article)
        comments @(subscribe :page/sub-comments {:page-path page-path
                                                 :slug slug})
        loading @(subscribe :app/loading)
        user @(subscribe :app/user)
        errors @(subscribe :app/errors)]
    ;; (println "in pages/article-page: " page-cache)
    [:div.article-page
     [:div.banner
      [:div.container
       [:h1 (:title article)]
       [base/view core :articles/article-meta article]]]      ;; defined in Helpers section
     [:div.container.page
      [:div.row.article-content
       [:div.col-md-12
        [:p (:body article)]]]
      [base/view core :tags/tags-list (:tagList article)] ;; defined in Helpers section
      [:hr]
      [:div.article-actions
       [base/view core :articles/article-meta article]]       ;; defined in Helpers section
      [:div.row
       [:div.col-xs-12.col-md-8.offset-md-2
        (when (:comments errors)
          [base/view core :errors/errors-list (:comments errors)]) ;; defined in Helpers section
        (if-not (empty? user)
          [:form.card.comment-form
           [:div.card-block
            [:textarea.form-control {:placeholder "Write a comment..."
                                     :rows        "3"
                                     :value       (:body new-comment)
                                     :on-change   #(let [comment-body (-> % .-target .-value)]
                                                     (dispatch :page/change-new-comment {:slug slug
                                                                                         :comment {:body comment-body}}))}]]
           [:div.card-footer
            [:img.comment-author-img {:src (:image user) :alt "user image"}]
            [:button.btn.btn-sm.btn-primary {:class    (when (:comments loading) "disabled")
                                             :on-click (fn [event]
                                                         (.preventDefault event)
                                                         (dispatch :api/post-comment {:slug slug
                                                                                      :comment new-comment
                                                                                      :on-success :page/post-comment-response})
                                                         (dispatch :page/delete-new-comment {:slug slug}))} "Post Comment"]]]
          [:p
           [:a {:href (router :path-for :page/register-page)} "Sign up"]
           " or "
           [:a {:href (router :path-for :page/login-page)} "Sign in"]
           " to add comments on this article."])
        (if (:comments loading)
          [:div
           [:p "Loading comments ..."]]
          (if (empty? comments)
            [:div]
            (for [{:keys [id createdAt body author]} comments]
              ^{:key id} [:div.card
                          [:div.card-block
                           [:p.card-text body]]
                          [:div.card-footer
                           [:a.comment-author {:href (router :path-for :page/profile-page {:user-id (:username author)})}
                            [:img.comment-author-img {:src (:image author) :alt "user image"}]]
                           " "
                           [:a.comment-author {:href (router :path-for :page/profile-page {:user-id (:username author)})} (:username author)]
                           [:span.date-posted (format-date createdAt)]
                           (when (= (:username user) (:username author))
                             [:span.mod-options {:on-click #(dispatch :api/delete-comment {:comment-id id
                                                                                           :slug slug
                                                                                           :on-success #:request {:method :page/delete-comment-response
                                                                                                                  :body {:comment-id id}}})}
                              [:i.ion-trash-a]])]])))]]]]))

;; - profile-page

(defmethod base/view :page/profile-page
  [{:keys [dispatch subscribe router] :as core} _method route]
  (let [{:keys [page-path path-params]} route
        {:keys [username]} path-params
        page-cache @(subscribe :page/sub-cache {:page-path page-path})
        {:keys [filter]} page-cache
        articles @(subscribe :page/sub-articles {:page-path page-path :offset (:offset filter) :limit (:limit filter)})
        {:keys [image username bio following] :or {username ""}} (get-in page-cache [:profiles username])
        {:keys [author favorites]} filter
        loading @(subscribe :app/loading)
        user @(subscribe :app/user)]
    [:div.profile-page
     [:div.user-info
      [:div.container
       [:div.row
        [:div.col-xs-12.col-md-10.offset-md-1
         [:img.user-img {:src image :alt "user image"}]
         [:h4 username]
         [:p bio]
         (if (= (:username user) username)
           [:a.btn.btn-sm.btn-outline-secondary.action-btn {:href (router :path-for :page/settings-page)}
            [:i.ion-gear-a] " Edit Profile Settings"]
           [:button.btn.btn-sm.action-btn.btn-outline-secondary {:on-click (if following
                                                                             #(dispatch :api/unfollow {:username username
                                                                                                       :on-success :page/follow-status-response})
                                                                             #(dispatch :api/follow {:username username
                                                                                                     :on-success :page/follow-status-response}))
                                                                 :class    (when (:toggle-following loading) "disabled")}
            [:i {:class (if following "ion-minus-round" "ion-plus-round")}]
            [:span (if following (str " Unfollow " username) (str " Follow " username))]])]]]]
     [:div.container
      [:div.row
       [:div.col-xs-12.col-md-10.offset-md-1
        [:div.articles-toggle
         [:ul.nav.nav-pills.outline-active
          [:li.nav-item
           [:a.nav-link {:href (router :path-for :page/profile-page {:username username}) :class (when author " active")} "My Articles"]]
          [:li.nav-item
           [:a.nav-link {:href (router :path-for :page/profile-page-favorites {:username username}) :class (when favorites "active")} "Favorited Articles"]]]]
        [base/view core :articles/articles-list {:articles articles
                                                 :loading-articles (:articles loading)}]]]]]))

(defmethod base/view :page/profile-page-favorites
  [core _method route]
  [base/view core :page/profile-page (assoc route :favorites true)])

;; - login-page

(defmethod base/view :page/login-page
  [{:keys [dispatch subscribe router] :as core} _ route]
  (let [{:keys [page-path]} route
        {:keys [email password] :as user-input} @(subscribe :page/sub-cache-item {:page-path page-path
                                                                                   :cache-path [:user-input]})
        loading @(subscribe :app/loading)
        errors @(subscribe :app/errors)]
    [:div.auth-page
     [:div.container.page
      [:div.row
       [:div.col-md-6.offset-md-3.col-xs-12
        [:h1.text-xs-center "Sign in"]
        [:p.text-xs-center
         [:a {:href (router :path-for :page/register-page)} "Need an account?"]]
        (when (:login errors)
          [base/view core :errors/errors-list (:login errors)])
        [:form {:on-submit (fn [event]
                             (.preventDefault event)
                             (dispatch :api/login {:user user-input
                                                   :on-success :page/login-response}))}
         [:fieldset.form-group
          [:input.form-control.form-control-lg {:type        "text"
                                                :placeholder "Email"
                                                :value       email
                                                :on-change   (fn [event]
                                                               (let [email (-> event .-target .-value)]
                                                                 (dispatch :page/set-cache-item
                                                                           {:cache-item [[:user-input :email] email]})))
                                                :disabled    (:login loading)}]]

         [:fieldset.form-group
          [:input.form-control.form-control-lg {:type        "password"
                                                :placeholder "Password"
                                                :value       password
                                                :on-change   (fn [event]
                                                               (let [password (-> event .-target .-value)]
                                                                 (dispatch :page/set-cache-item
                                                                           {:cache-item [[:user-input :password] password]})))
                                                :disabled    (:login loading)}]]
         [:button.btn.btn-lg.btn-primary.pull-xs-right {:class (when (:login loading) "disabled")} "Sign in"]]]]]]))

;; - register-page

(defmethod base/view :page/register-page
  [{:keys [dispatch subscribe router] :as core} _ route]
  (let [{:keys [page-path]} route
        {:keys [username email password] :as user-input} @(subscribe :page/sub-cache-item {:page-path page-path
                                                                                            :cache-path [:user-input]})
        loading @(subscribe :app/loading)
        errors @(subscribe :app/errors)]
    [:div.auth-page
     [:div.container.page
      [:div.row
       [:div.col-md-6.offset-md-3.col-xs-12
        [:h1.text-xs-center "Sign up"]
        [:p.text-xs-center
         [:a {:href (router :path-for :page/login-page)} "Have an account?"]]
        (when (:register-user errors)
          [base/view core :errors/errors-list (:register-user errors)])
        [:form {:on-submit (fn [event]
                             (.preventDefault event)
                             (dispatch :api/register-user {:user user-input
                                                           :on-success :page/register-user-response}))}
         [:fieldset.form-group
          [:input.form-control.form-control-lg {:type        "text"
                                                :placeholder "Your Name"
                                                :value       username
                                                :on-change   #(let [username (-> % .-target .-value)]
                                                                (dispatch :page/set-cache-item
                                                                          {:cache-item [[:user-input :username] username]}))
                                                :disabled    (:register-user loading)}]]
         [:fieldset.form-group
          [:input.form-control.form-control-lg {:type        "text"
                                                :placeholder "Email"
                                                :value       email
                                                :on-change   #(let [email (-> % .-target .-value)]
                                                                (dispatch :page/set-cache-item
                                                                          {:cache-item [[:user-input :email] email]}))
                                                :disabled    (:register-user loading)}]]
         [:fieldset.form-group
          [:input.form-control.form-control-lg {:type        "password"
                                                :placeholder "Password"
                                                :value       password
                                                :on-change   #(let [password (-> % .-target .-value)]
                                                                (dispatch :page/set-cache-item
                                                                          {:cache-item [[:user-input :password] password]}))
                                                :disabled    (:register-user loading)}]]
         [:button.btn.btn-lg.btn-primary.pull-xs-right {:class (when (:register-user loading) "disabled")} "Sign up"]]]]]]))


;; - settings-page

(defmethod base/view :page/settings-page
  [{:keys [dispatch subscribe]} _ route]
  (let [{:keys [page-path]} route
        user @(subscribe :app/user)
        loading @(subscribe :app/loading)
        user-input @(subscribe :page/sub-cache-item {:page-path page-path
                                                     :cache-path [:user-input]})]
    [:div.settings-page
     [:div.container.page
      [:div.row
       [:div.col-md-6.offset-md-3.col-xs-12
        [:h1.text-xs-center "Your Settings"]
        [:form
         [:fieldset
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "URL of profile picture"
                                 :default-value (:image user)
                                 :on-change     #(dispatch :page/set-cache-item {:cache-item [[:user-input :image] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Your Name"
                                                 :default-value (:username user)
                                                 :on-change    #(dispatch :page/set-cache-item {:cache-item [[:user-input :username] (-> % .-target .-value)]})
                                                 :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:textarea.form-control.form-control-lg {:rows          "8"
                                                    :placeholder   "Short bio about you"
                                                    :default-value (:bio user)
                                                    :on-change     #(dispatch :page/set-cache-item {:cache-item [[:user-input :bio] (-> % .-target .-value)]})
                                                    :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Email"
                                                 :default-value (:email user)
                                                 :on-change     #(dispatch :page/set-cache-item {:cache-item [[:user-input :email] (-> % .-target .-value)]})
                                                 :disabled      (:update-user loading)}]]
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "password"
                                                 :placeholder   "Password"
                                                 :default-value ""
                                                 :on-change     #(dispatch :page/set-cache-item {:cache-item [[:user-input :password] (-> % .-target .-value)]})
                                                 :disabled      (:update-user loading)}]]
          [:button.btn.btn-lg.btn-primary.pull-xs-right {:on-click (fn [event]
                                                                     (.preventDefault event)
                                                                     (dispatch :api/update-user {:user user-input
                                                                                                 :on-success :page/update-user-response}))
                                                         :class    (when (:update-user loading) "disabled")} "Update Settings"]]]
        [:hr]
        [:button.btn.btn-outline-danger {:on-click (fn [event]
                                                     (.preventDefault event)
                                                     (dispatch :login-page/logout {}))} "Or click here to logout."]]]]]))

;; - editor-page

(defmethod base/view :page/editor-new-page
  [{:keys [dispatch subscribe] :as core} _ route]
  (let [{:keys [page-path]} route
        article-input @(subscribe :page/sub-cache-item {:page-path page-path
                                                         :cache-path [:article-input]})
        errors @(subscribe :app/errors)]
    [:div.editor-page
     [:div.container.page
      [:div.row
       [:div.col-md-10.offset-md-1.col-xs-12
        (when (:upsert-article errors)
          [base/view core :errors/errors-list (:upsert-article errors)])
        [:form
         [:fieldset
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Article Title"
                                                 :default-value ""
                                                 :on-change     #(dispatch :page/set-cache-item
                                                                           {:cache-item [[:article-input :title] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "What's this article about?"
                                 :default-value ""
                                 :on-change     #(dispatch :page/set-cache-item
                                                           {:cache-item [[:article-input :description] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:textarea.form-control {:rows          "8"
                                    :placeholder   "Write your article (in markdown)"
                                    :default-value ""
                                    :on-change    #(dispatch :page/set-cache-item
                                                             {:cache-item [[:article-input :body] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "Enter tags"
                                 :default-value ""
                                 :on-change     #(dispatch :page/set-cache-item
                                                           {:cache-item [[:article-input :tagList] (-> % .-target .-value)]})}]
           [:div.tag-list]]
          [:button.btn.btn-lg.btn-primary.pull-xs-right {:on-click (fn [event]
                                                                     (.preventDefault event)
                                                                     (dispatch :api/create-article {:article-input article-input
                                                                                                    :on-success :page/create-article-response}))}
           "Publish Article"]]]]]]]))

(defmethod base/view :page/editor-article-page
  [{:keys [dispatch subscribe] :as core} _ route]
  (let [{:keys [page-path path-params]} route
        {:keys [slug]} path-params
        article-input @(subscribe :page/sub-cache-item {:page-path page-path
                                                        :cache-path [:article-input]})
        {:keys [title description body tagList]} article-input
        errors @(subscribe :app/errors)]
    [:div.editor-page
     [:div.container.page
      [:div.row
       [:div.col-md-10.offset-md-1.col-xs-12
        (when (:upsert-article errors)
          [base/view core :errors/errors-list (:upsert-article errors)])
        [:form
         [:fieldset
          [:fieldset.form-group
           [:input.form-control.form-control-lg {:type          "text"
                                                 :placeholder   "Article Title"
                                                 :value title
                                                 :on-change     #(dispatch :page/set-cache-item
                                                                           {:cache-item [[:article-input :title] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "What's this article about?"
                                 :value description
                                 :on-change     #(dispatch :page/set-cache-item
                                                           {:cache-item [[:article-input :description] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:textarea.form-control {:rows          "8"
                                    :placeholder   "Write your article (in markdown)"
                                    :value body
                                    :on-change    #(dispatch :page/set-cache-item
                                                             {:cache-item [[:article-input :body] (-> % .-target .-value)]})}]]
          [:fieldset.form-group
           [:input.form-control {:type          "text"
                                 :placeholder   "Enter tags"
                                 :value tagList
                                 :on-change     #(dispatch :page/set-cache-item
                                                           {:cache-item [[:article-input :tagList] (-> % .-target .-value)]})}]
           [:div.tag-list]]
          [:button.btn.btn-lg.btn-primary.pull-xs-right {:on-click (fn [event]
                                                                     (.preventDefault event)
                                                                     (dispatch :api/update-article {:slug    slug
                                                                                                    :article-input article-input
                                                                                                    :on-success :page/update-article-response}))}
           "Update Article"]]]]]]]))

;; -- App level views -----------------------------------------------------------------

(defmethod base/view :app/current-page
  [{:keys [router] :as core} _ ]
  (let [
        route-value @(router :current-route-atom)
        page-name (:page-name route-value)]
    [:<>
     [base/view core :common/header route-value]
     [base/view core page-name route-value]
     [base/view core :common/footer route-value]]))
