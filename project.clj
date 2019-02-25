(defproject rao "0.1.0-SNAPSHOT"
  :description "Simple pattern to organize the state in your rum application."
  :url "https://github.com/bensu/rao"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [rum "0.11.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.10.520"]]
                   :plugins      [[lein-figwheel "0.5.18"]]
                   :cljsbuild    {:builds [{:id           "todo"
                                            :source-paths ["src/" "examples/todo/src/"]
                                            :figwheel     true
                                            :compiler     {:main       "examples.todo"
                                                           :asset-path "js/todo-out"
                                                           :output-to  "resources/public/js/todo.js"
                                                           :output-dir "resources/public/js/todo-out"}}]}}})
