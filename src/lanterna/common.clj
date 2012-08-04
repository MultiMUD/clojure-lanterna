(ns lanterna.common
  (:import com.googlecode.lanterna.input.Key)
  (:require [lanterna.constants :as c]))


(defn parse-key [^Key k]
  (when k
    (let [kind (c/key-codes (.getKind k))]
      (if (= kind :normal)
        (.getCharacter k)
        kind))))

