(ns todomvc-ratom.view
  (:require
   [reagent.core  :as reagent]
   [clojure.string :as str]
   [fancoil.base :as base]))


(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val  (reagent/atom title)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (on-save v)
                (stop))]
    (fn [props]
      [:input (merge (dissoc props :on-save :on-stop :title)
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))


(defn todo-item
  [{:keys [dispatch]} todo]
  (let [editing (reagent/atom false)]
    (fn [env {:keys [id done title]}]
      [:li {:class (str (when done "completed ")
                        (when @editing "editing"))}
       [:div.view
        [:input.toggle
         {:type "checkbox"
          :checked done
          :on-change #(dispatch :toggle-done {:id id})}]
        [:label
         {:on-double-click #(reset! editing true)}
         title]
        [:button.destroy
         {:on-click #(dispatch :delete-todo {:id id})}]]
       (when @editing
         [todo-input {:class "edit"
                      :title title
                      :on-save #(if (seq %)
                                  (dispatch :save {:id id
                                                   :title %})
                                  (dispatch :delete-todo {:id id}))
                      :on-stop #(reset! editing false)}])])))


(defn task-list
  [{:keys [dispatch subscribe] :as env}]
  (let [visible-todos @(subscribe :visible-todos)
        all-complete? @(subscribe :all-complete?)]
    [:section#main
     [:input#toggle-all
      {:type "checkbox"
       :checked all-complete?
       :on-change #(dispatch :complete-all-toggle)}]
     [:label
      {:for "toggle-all"}
      "Mark all as complete"]
     [:ul#todo-list
      (for [todo  visible-todos]
        ^{:key (:id todo)} [todo-item env todo])]]))


(defn footer-controls
  [{:keys [dispatch subscribe]}]
  (let [[active done] @(subscribe :footer-counts)
        showing       @(subscribe :showing)
        a-fn          (fn [filter-kw txt]
                        [:a {:class (when (= filter-kw showing) "selected")
                             :on-click #(dispatch [:set-showing (keyword filter-kw)])}
                         txt])]
    [:footer#footer
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li (a-fn :all    "All")]
      [:li (a-fn :active "Active")]
      [:li (a-fn :done   "Completed")]]
     (when (pos? done)
       [:button#clear-completed {:on-click #(dispatch :clear-completed)}
        "Clear completed"])]))


(defn task-entry
  [{:keys [dispatch]}]
  [:header#header
   [:h1 "todos"]
   [todo-input
    {:id "new-todo"
     :placeholder "What needs to be done?"
     :on-save #(when (seq %)
                 (dispatch :add-todo {:text %}))}]])


(defn todo-app
  [{:keys [subscribe] :as env} props]
  [:<>
   [:section#todoapp
    [task-entry env]
    (when (seq @(subscribe :todos))
      [task-list env])
    [footer-controls env]]
   [:footer#info
    [:p "Double-click to edit a todo"]]])


(defmethod base/view :todo-app
  [env _ props]
  [todo-app env props])