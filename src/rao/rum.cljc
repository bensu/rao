(ns rao.rum
  (:require [rum.core :as rum]))

(defn wire
  "Creates a dispatch, `d!`, to update the component with. Whenever `d!` is called it will:

   1. update that rao/local state with `step` and
   2. call `effect!` to do any side-effects

   0. when the component is started, it adds an atom rao/local from the value of `initial-state`.

  `initial-state` can be:
     - a map with the initial state to the component.
     - a function, it will be called with the :rum/args to the component and expected to return the initial state."
  ([initial-state step]
   (wire initial-state step nil))
  ([initial-state step effect!]
   {:pre [(or (map? initial-state) (ifn? initial-state))
          (ifn? step)
          (or (nil? effect!) (ifn? effect!))]}
   {:init (cond
            (map? initial-state) (fn init [rum-state _]
                                   (assoc rum-state :rao/local (atom initial-state)))
            (ifn? initial-state) (fn init [{:keys [rum/args] :as rum-state} _]
                                   (assoc rum-state :rao/local (atom (apply initial-state args))))
            :else (throw (ex-info "init-state needs to be either a map or a function" {})))
    :will-mount (fn [{:keys [rao/local rum/react-component] :as rum-state}]
                  #?(:cljs
                     (add-watch local :rao/local (fn [_ _ old-value new-value]
                                                   (when-not (= old-value new-value)
                                                     (rum/request-render react-component)))))
                  (let [parent-d! (:rao/d! (first (:rum/args rum-state)))]
                    (assoc rum-state
                           :rao/state @local
                           :rao/d! (fn d! [action data]
                                     (let [state' (swap! local step [action data])]
                                       (when effect!
                                         (effect! state' [action data {:rao/d! d!}]))
                                       (when parent-d!
                                         (parent-d! action data {:rao/d! parent-d!})))))))
    :before-render (fn [{:keys [rao/local] :as rum-state}]
                     (assoc rum-state :rao/state @local))}))
