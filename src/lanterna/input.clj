(ns lanterna.input
  (:require 
    [lanterna.constants :as c]
    [lanterna.protocols :as prot])
  (:import 
    [com.googlecode.lanterna.input InputProvider KeyStroke]
    [lanterna.protocols Input]))

(defn- parse-key 
  "Return a character or keyword representing the KeyStroke"
  [^KeyStroke k]
  (if (= :normal (c/key-codes (.getKeyType k)))
    (.getCharacter k)
    (c/key-codes (.getKeyType k))))


(defn get-keystroke
  "Get the next keypress from the user, or nil if none are buffered."
  [^InputProvider provider]
  (let [key (.pollInput provider)]
    (if (nil? key)
      key
      {:key (parse-key key) :ctrl (.isCtrlDown key) :alt (.isAltDown key) :shift (.isShiftDown key)})))

(defn get-keystroke-blocking
  "Get the next keypress from the user. If no keypresses are buffered, this
  function will block."
  ([^InputProvider provider]
   (let [key (.readInput provider)]
     {:key (parse-key key) :ctrl (.isCtrlDown key) :alt (.isAltDown key) :shift (.isShiftDown key)})))

(extend-type InputProvider 
  prot/Input
  (-poll-stroke [this]
    (get-keystroke this))
  (-get-stroke [this]
    (get-keystroke-blocking this)))
