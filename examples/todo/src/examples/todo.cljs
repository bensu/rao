(ns examples.todo
  (:require [goog.dom :as dom]
            [rao.rum :as rao]
            [rum.core :as rum :refer [defc defcs]]))

;; ======================================================================
;; todo app

(enable-console-print!)

(defonce db
  (atom
    {:items [{:id 0 :content "a"}
             {:id 1 :content "b"}
             {:id 2 :content "c"}]}))

(defn item-step [state [action data]]
  (case action
    :item/start-editing (assoc state :item/text (:item/text data))
    :item/edit (assoc state :item/text (:item/text data))
    :item/stop-editing (dissoc state :item/text)
    state))

(defcs render-item <

  {:key-fn (comp :id :item)}
  (rao/wire {} item-step)

  [{:keys [rao/state rao/d!]} {:keys [item]}]

  [:li {}
   (if-let [editing-text (:item/text state)]
     [:input {:type "text"
              :value editing-text
              :auto-focus true
              :on-blur (fn [e]
                         (d! :item/stop-editing {:item/text (.. e -target -value)}))
              :on-change (fn [e]
                           (d! :item/edit {:item/text (.. e -target -value)}))}]
     [:p {:on-click (fn [_]
                      (d! :item/start-editing {:item/text (:content item)}))}
      (:content item)])])

(defn ->duplicated
  "Returns repeated pairs"
  [items]
  (let [repeated-pairs (->> items
                            (group-by :content)
                            (keep (fn [[_ items]]
                                    (when (< 1 (count items))
                                      (set (map :id items))))))]
    (when-not (empty? repeated-pairs)
      (vec repeated-pairs))))

(defn list-step [state [action data metadata]]
  (case action
    :item/stop-editing (let [{:keys [item/text]} data]
                         (let [item-id (:rao/id metadata)
                               state (assoc-in state [:items item-id :content] text)]
                           (if-let [pairs (->duplicated (:items state))]
                             (assoc state :error (str "you have duplicated todos " (pr-str pairs)))
                             (dissoc state :error))))
    state))

(defcs render-list <

  (rao/wire db list-step)

  [{:keys [rao/d! rao/state]}]

  [:div {}
   [:ul {}
    (for [item (:items state)]
      (render-item {:item item :rao/d! d! :rao/id (:id item)}))]
   (when-let [error (:error state)]
     [:div {}
      [:p {} "There was an error: " error]])])

(rum/mount (render-list) (dom/getElement "main-area"))
