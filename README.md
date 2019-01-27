# rao

Simple pattern to organize the state in your rum application.

## Rationale

React models web UIs as pure functions of state. We write a function that given
some `state` will return a representation of HTML, and then React will make sure
that our HTML makes it into the webpage somehow. React doesn't tell us how to
change the `state` so that the UI changes.

Within the JavaScript world there are several options: Flux, Redux, etc. ClojureScript
also has many options: Reagent/re-frame, om.next, etc. Elm has one way, the Elm
Architecture. I like the Elm Architecture and I would like to follow a similar pattern
when using rum, a very simple React wrapper. rao offers that.

## Usage


```clj
(ns my.project
  (:require [rum.core :as rum :refer [defc defcs]]
            [rao.rum :as rao]))

;; Your application starts with this value as local state

(def initial-state {:counter 0})

;; Every time you want the state to change, you use the function `d!`,
;; for dispatch event:

;;   (d! :action {:data "something"})

;; That will:

;; 1. Apply `step` to the current `state` of the application
;; `step` should be a pure function, free of side-effects

;; 2. Replace the current state with whatever `step` returned

(defn step [state [action data]]
  (case action
    :count (update state :counter inc)))

;; 3. Call `effect!` where we get a chance to do side-effects:

(defn effect! [state [action data]]
  (case action
    :count (println "you counted")
    nil))

;; 4. Re-render the component with the new `state`

(defcs my-counter <

  ;; we wire `initial-state`, `step`, `effect!` things together
  ;; in a mixin
  (rao/wire initial-state step effect!)

  ;; the render function of the component now has access to
  ;; the `state` and `d!`, the dispatch function that kicks off the
  ;; rao process

  [{:keys [rao/state rao/d!]} props]

  [:div
    [:h1 "my counter "]
    [:h2 (:subtitle props)]
    [:pre (:counter state)]
    [:button {:on-click (fn [_]
                          (d! :count {}))}]])

```

## License

Copyright © 2019 Sebastian Bensusan

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
