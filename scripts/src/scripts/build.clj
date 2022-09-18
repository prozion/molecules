#_(defdeps
    [[org.clojure/clojure "1.9.0"]
    [org.clojars.prozion/odysseus "0.1.7"]
    [org.clojars.prozion/tabtree "0.6.8"]
    [org.clojure/data.csv "1.0.1"]])

(ns scripts.build
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [scripts.hyperchem :refer :all]
            [scripts.graphml :refer :all]
            ))
