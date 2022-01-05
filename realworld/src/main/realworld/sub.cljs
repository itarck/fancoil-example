(ns realworld.sub
  (:require
   [reagent.core :as r]
   [fancoil.base :as base]))


;; pages

(defmethod base/subscribe :page/sub-cache
  [{:keys [ratom]} _ props]
  (let [{:keys [page-path]} props]
    (r/cursor ratom [:pages page-path])))

(defmethod base/subscribe :page/sub-cache-item
  [{:keys [ratom]} _ props]
  (let [{:keys [cache-path page-path]} props]
    (r/cursor ratom (concat [:pages page-path] cache-path))))

(defmethod base/subscribe :page/sub-articles
  [{:keys [ratom] :as core} _ props]
  (let [{:keys [offset limit page-path] :or {offset 0 limit 10}} props
        articles-val (->> (vals @(base/subscribe core :page/sub-cache-item {:page-path page-path
                                                                            :cache-path [:articles]}))
                          (sort-by :createdAt)
                          reverse)
        articles (if (seq articles-val)
                   (take limit (drop offset articles-val))
                   [])]
    (r/reaction articles)))

(defmethod base/subscribe :page/sub-comments
  [{:keys [ratom] :as core} _ props]
  (let [{:keys [slug page-path]} props
        comments (vals @(base/subscribe core :page/sub-cache-item {:page-path page-path
                                                                   :cache-path [:articles slug :comments]}))]
    (r/reaction (reverse (sort-by :id comments)))))

;; app 

(defmethod base/subscribe :app/loading
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:loading]))

(defmethod base/subscribe :app/user
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:user]))

(defmethod base/subscribe :app/errors
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:errors]))

(defmethod base/subscribe :app/current-route
  [{:keys [ratom]} _ _]
  (r/cursor ratom [:current-route]))