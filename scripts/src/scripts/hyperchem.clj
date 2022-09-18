(ns scripts.hyperchem
  (:require [clojure.test :refer :all]
            [clojure.string :as s]
            [org.clojars.prozion.odysseus.io :as io]
            [org.clojars.prozion.odysseus.utils :refer :all]
            [org.clojars.prozion.odysseus.text :refer :all]
            [org.clojars.prozion.odysseus.debug :refer :all]
            [org.clojars.prozion.tabtree.tabtree :as tabtree]
            [org.clojars.prozion.tabtree.rdf :as rdf]
            [org.clojars.prozion.tabtree.output :as output]
            [org.clojars.prozion.tabtree.utils :as utils]
            ))

(defn parse-neighbours [neighbours-str]
  (let [neighbours (s/split neighbours-str #" ")
        neighbours (partition 2 neighbours)
       ]
    (reduce
      (fn [acc [atom-index bond-type]]
        (case bond-type
          "s" (merge-with conj acc {:s atom-index})
          "d" (merge-with conj acc {:d atom-index})
          (merge-with conj acc {:t atom-index})))
      {:s [] :d [] :t []}
      neighbours)))

(defn bond-index-to-id [index tabtree]
  (->> tabtree
      (filter (fn [[id item]] (= (->number index) (:index item))))
      vals
      first
      :__id))

(defn substitute-indexes-to-ids [item tabtree]
  (let [index (->number (:index item))
        s-bonds (filter (fn [n] (> (->number n) index)) (:s item))
        d-bonds (filter (fn [n] (> (->number n) index)) (:d item))
        t-bonds (filter (fn [n] (> (->number n) index)) (:t item))
        s-ids (map #(bond-index-to-id % tabtree) s-bonds)
        d-ids (map #(bond-index-to-id % tabtree) d-bonds)
        t-ids (map #(bond-index-to-id % tabtree) t-bonds)
        item (apply dissoc item [:s :d :t])
        res
          (merge
            item
            (when (not-empty? s-ids) {:s s-ids})
            (when (not-empty? d-ids) {:d d-ids})
            (when (not-empty? t-ids) {:t t-ids}))]
    res))

(defn hin-to-tabtree [hin-file]
  (let [hin-content (io/read-file-by-lines hin-file)
        tabtree-1 (reduce
                    (fn [acc line]
                      (cond
                        (re-seq #"^atom.*?" line)
                          (let [
                                [[_ index name element type flags charge x y z cn neigbours]]
                                  (re-seq
                                    #"^atom (\d+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) ([\S]+?) (.*?)$"
                                    line)
                                ; _ (--- _ index name element type flags charge x y z cn neigbours)
                                ; _ (soft-exit)
                                name (if (= name "-") nil name)
                                id (keyword (format "%s%s" element index))
                                item {:__id id :index (->number index) :a (keyword element)}
                                item (merge item (parse-neighbours neigbours))]
                            (merge acc {id item}))
                        :else acc))
                    {}
                    hin-content)
        tabtree-2 (into {} (map (fn [[id item]] [id (substitute-indexes-to-ids item tabtree-1)]) tabtree-1))]
    tabtree-2))
