(ns scripts.graphml
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [org.clojars.prozion.odysseus.io :refer :all]
            [org.clojars.prozion.odysseus.utils :refer :all]
            [org.clojars.prozion.odysseus.text :refer :all]
            [org.clojars.prozion.odysseus.debug :refer :all]
            [org.clojars.prozion.tabtree.tabtree :as tabtree]
            [org.clojars.prozion.tabtree.rdf :as rdf]
            [org.clojars.prozion.tabtree.output :as output]
            [org.clojars.prozion.tabtree.utils :as utils]
            ))

(defn make-edges [bond-type root-id ids]
  (if (or (not ids) (empty? ids))
    ""
    (reduce
      (fn [acc id]
        (format "%s<edge source=\"%s\" target=\"%s\"><data key=\"label\">%s</data></edge>\n\t\t"
                acc
                (name root-id)
                (name id)
                (case bond-type
                  :single "single"
                  :double "double"
                  :triple "triple"
                  "chemical bond")
                ))
      ""
      ids)))

(defn make-graphml [tabtree]
  (let [graphml (slurp "templates/graphml.xml")
        nodes (filter-map (fn [[id item]] (index-of? [:H :C :O :N :P :S] (item :a))) tabtree)
        nodes-xml (reduce
                (fn [acc [id item]]
                  (format "%s<node id=\"%s\" labels=\"%s\"><data key=\"name\">%s</data></node>\n\t\t" acc (name id) (-> item :a name) (-> item :a name)))
                ""
                nodes)
        edges-xml (reduce
                (fn [acc [id item]]
                  (let [
                        single-bonds-edges (make-edges :single id (:s item))
                        double-bonds-edges (make-edges :double id (:d item))
                        triple-bonds-edges (make-edges :triple id (:t item))]
                  (format "%s%s%s%s" acc single-bonds-edges double-bonds-edges triple-bonds-edges)))
                ""
                nodes)
        graphml (s/replace graphml "{{nodes}}" nodes-xml)
        graphml (s/replace graphml "{{edges}}" edges-xml)
        ]
    graphml))
