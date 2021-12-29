(ns todomvc-datascript.view
  (:require
   [reagent.core :as r]
   [fancoil.base :as base]))


(defn TodoEdit [{:keys [title on-save on-stop]}]
  (let [val (r/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class]}]
      [:input {:type "text" :value @val
               :id id :class class
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(defn TodoView
  [props {:keys [dispatch subscribe]}]
  (let [editing (r/atom false)]
    (fn [{:keys [todo]} _env]
      (let [todo @(subscribe :todo/pull-one {:id (:db/id todo)})
            {:todo/keys [status title]} todo
            id (:db/id todo)
            done (= status :done)]

        [:li {:class (str (if done "completed ")
                          (if @editing "editing"))}
         [:div.view
          [:input.toggle {:type "checkbox" :checked done
                          :on-change #(dispatch :todo/toggle-status {:todo todo})}]
          [:label {:on-double-click #(reset! editing true)} title]
          [:button.destroy {:on-click #(dispatch :todo/delete {:id id})}]]
         (when @editing
           [TodoEdit {:class "edit" :title title
                      :on-save #(dispatch :todo/set-title {:id id
                                                           :value %})
                      :on-stop #(reset! editing false)}])]))))


(defn TodoInput [{:keys [title on-save on-stop]}]
  (let [val (r/atom title)
        stop #(do (reset! val "")
                  (if on-stop (on-stop)))
        save #(let [v (-> @val str clojure.string/trim)]
                (if-not (empty? v) (on-save v))
                (stop))]
    (fn [{:keys [id class placeholder]}]
      [:input {:type "text" :value @val
               :id id :class class :placeholder placeholder
               :on-blur save
               :on-change #(reset! val (-> % .-target .-value))
               :on-key-down #(case (.-which %)
                               13 (save)
                               27 (stop)
                               nil)}])))

(defn TodoStatusBar 
  [{:keys [filt active done clear-done]}]
  (let [props-for (fn [name]
                    {:class (if (= name @filt) "selected")
                     :on-click #(reset! filt name)})]
    [:div
     [:span#todo-count
      [:strong active] " " (case active 1 "item" "items") " left"]
     [:ul#filters
      [:li [:a (props-for :all) "All"]]
      [:li [:a (props-for :active) "Active"]]
      [:li [:a (props-for :done) "Completed"]]]
     (when (pos? done)
       [:button#clear-completed {:on-click clear-done}
        "Clear completed " done])]))


(defn TodoListView
  [{:keys [todolist]} {:keys [subscribe dispatch] :as env}]
  (let [filt (r/atom :all)]
    (fn []
      (let [todolist-1 @(subscribe :todolist/pull-one {:id (:db/id todolist)})
            todos (:todo/_todolist todolist-1)
            done @(subscribe :todolist/filter-todo-ids {:todolist todolist-1 :status :done})
            active @(subscribe :todolist/filter-todo-ids {:todolist todolist-1 :status :active})]
        [:div#todo.large-paper
         [:section#todoapp
          [:header#header
           [:h1 "todos"]
           [TodoInput {:id "new-todo"
                       :placeholder "What needs to be done?"
                       :on-save #(dispatch :todolist/create-todo {:todolist todolist-1
                                                                  :title %})}]]
          (when (-> todos count pos?)
            [:div
             [:section#main
              [:input#toggle-all {:type "checkbox" :checked (empty? active)
                                  :on-change #(dispatch :todolist/toggle-all {:todolist todolist-1
                                                                              :status (if (seq active) :done :active)})}]
              [:label {:for "toggle-all"} "Mark all as complete"]
              [:ul#todo-list
               (for [todo (filter (case @filt
                                    :active #(= (:todo/status %) :active)
                                    :done #(= (:todo/status %) :done)
                                    :all identity)
                                  todos)]
                 ^{:key (:db/id todo)} [TodoView {:todo todo} env])]]
             [:footer#footer
              [TodoStatusBar {:active (count active) :done (count done) :filt filt
                              :clear-done #(dispatch :todolist/clear-done {:todolist todolist-1})}]]])]
         [:footer#info
          [:p "Double-click to edit a todo"]]]))))


(defmethod base/view :todolist/view
  [config _ props]
  [TodoListView props config])