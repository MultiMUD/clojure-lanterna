(ns lanterna.common
  (:import com.googlecode.lanterna.input.Key)
  (:require [lanterna.constants :as c]))


(defn parse-key [^Key k]
  (when k
    (let [kind (c/key-codes (.getKind k))]
      (if (= kind :normal)
        (.getCharacter k)
        kind))))

(defn block-on
  "Repeatedly apply func to args until a non-nil value is returned.

  Options can include any of the following keys:

  :interval - sets the interval between function applications (default 50)
  :timeout  - sets the maximum amount of time blocking will occur before
              returning nil

  "
  ([func args] (block-on func args {}))
  ([func args {:as opts
               :keys [interval timeout]
               :or {interval 50
                    timeout Double/POSITIVE_INFINITY}}]
     (loop [timeout timeout]
       (when (pos? timeout)
         (let [val (apply func args)]
           (if (nil? val)
             (do (Thread/sleep interval)
                 (recur (- timeout interval)))
             val))))))
