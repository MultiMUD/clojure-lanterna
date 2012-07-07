(ns lanterna.common
  (:require [lanterna.constants :as c]))


(defn parse-key [k]
  (when k
    (let [kind (c/key-codes (.getKind k))]
      (if (= kind :normal)
        (.getCharacter k)
        kind))))

