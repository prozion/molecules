(defproject molecular-graphs "0.0.1"
  :description "Molecules as graphs"
  :url "https://github.com/prozion/molecules"
  :license {:name "some private license"}
  :dependencies [
                [org.clojars.prozion/odysseus "0.1.7"]
                [org.clojars.prozion/tabtree "0.6.8"]
                [org.clojure/data.csv "1.0.1"]
                ]
  :plugins [
            ; [lein-ancient "0.6.15"]
            [lein-oneoff "0.3.2"]
            ]
  ; :main scripts.build
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.9.0"]]}}
  :release-tasks [
                  ["build-graphml"]
                  ]
  :repl-options {
    :init-ns scripts.repl
  }
)
