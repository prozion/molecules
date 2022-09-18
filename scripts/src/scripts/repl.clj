(ns scripts.repl
  (:require
            [clojure.string :as s]
            [scripts.hyperchem :refer :all]
            [scripts.graphml :refer :all]
            [org.clojars.prozion.odysseus.debug :refer :all]
            [org.clojars.prozion.tabtree.output :as output]
            ))

(defn run []
  (let [tabtree (hin-to-tabtree "../graphs/DeGly.hin")]
    (spit "../graphs/DeGly.tree"
          (output/tabtree->string
            tabtree
            :sorter ((fn [tabtree]
                      (fn [id1 id2]
                        (let [item1 (tabtree id1)
                              item2 (tabtree id2)
                              val1 (item1 :index)
                              val2 (item2 :index)
                              ]
                          (compare val1 val2))))
                    tabtree)))
    (spit
      "../graphs/DeGly.xml"
      (make-graphml tabtree))))
