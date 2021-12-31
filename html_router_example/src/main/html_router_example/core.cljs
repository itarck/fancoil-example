(ns html-router-example.core
  (:require
   [reagent.dom :as rdom]
   [fancoil.base :as base]
   [fancoil.unit :as fu]
   [integrant.core :as ig]
   [fancoil.module.reitit.html-router.core]))

;; -------------------------
;; Page components


(defmethod base/view :home-page
  [{:keys [router]} _ _]
  [:span.main
   [:h1 "Welcome to hello-router"]
   [:ul
    [:li [:a {:href (router :router/path-for :items-page)} "Items of hello-router"]]
    [:li [:a {:href "/broken/link"} "Broken link"]]]])


(defmethod base/view :items-page
  [{:keys [router]} _ _]
  [:span.main
   [:h1 "The items of hello-router"]
   [:ul (map (fn [item-id]
               [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                [:a {:href (router :router/path-for :item-page {:item-id item-id})} "Item: " item-id]])
             (range 1 60))]])


(defmethod base/view :item-page
  [{:keys [router]} _ {:keys [path-params]}]
  (let [item-id (get-in path-params [:item-id])]
    (println "item-page: " path-params)
    [:span.main
     [:h1 (str "Item " item-id " of hello-router")]
     [:p [:a {:href (router :router/path-for :items-page)} "Back to the list of items"]]]))


(defmethod base/view :about-page
  [_ _ _]
  [:span.main
   [:h1 "About hello-router"]])


;; -------------------------
;; Page mounting component


(defmethod base/view :current-page
  [{:keys [router] :as core} _ _]
  (let [current-route-value @(router :router/current-route-atom)
        page-name (:page-name current-route-value)]
    [:div
     [:header
      [:p [:a {:href (router :router/path-for :home-page)} "Home"] " | "
       [:a {:href (router :router/path-for :about-page)} "About hello-router"]]]
     [base/view core page-name current-route-value]
     [:footer
      [:p "hello-router was generated by the "
       [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"] "."]]]))


;; integrant

(def routes
  [["/" :home-page]
   ["/items"
    ["" :items-page]
    ["/:item-id" :item-page]]
   ["/about" :about-page]])

(derive ::router :reitit/html-router)

(def config
  {::fu/ratom {:initial-value {}}
   ::fu/inject {:ratom (ig/ref ::fu/ratom)}
   ::fu/do! {:ratom (ig/ref ::fu/ratom)}
   ::fu/handle {}
   ::fu/process {:ratom (ig/ref ::fu/ratom)
                 :handle (ig/ref ::fu/handle)
                 :inject (ig/ref ::fu/inject)
                 :do! (ig/ref ::fu/do!)}
   ::fu/subscribe {:ratom (ig/ref ::fu/ratom)}
   ::fu/view {:dispatch (ig/ref ::fu/dispatch)
              :subscribe (ig/ref ::fu/subscribe)
              :router (ig/ref ::router)}
   ::fu/chan {}
   ::fu/dispatch {:out-chan (ig/ref ::fu/chan)}
   ::fu/service {:process (ig/ref ::fu/process)
                 :in-chan (ig/ref ::fu/chan)}
   ::router {:routes routes}})

(def system
  (ig/init config))

;; -------------------------
;; Initialize app

(defn mount-root []
  (rdom/render
   [(::fu/view system) :current-page]
   (.getElementById js/document "app")))

(defn init! []
  (mount-root))

