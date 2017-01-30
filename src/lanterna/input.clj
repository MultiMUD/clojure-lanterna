(ns lanterna.input
  (:require [lanterna.constants :as c])
  (:import com.googlecode.lanterna.input.InputProvider
           com.googlecode.lanterna.input.KeyStroke))

(defn parse-key [^KeyStroke k]
  "Return a character or keyword representing the KeyStroke"
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

(defn get-key
  "Get the next character from the user sans modifiers, or nil if none are buffered."
  [^InputProvider provider]
  (let [keystroke (get-keystroke provider)]
    (if (nil? keystroke)
      keystroke
      (:key keystroke))))

(defn get-keystroke-blocking
  "Get the next keypress from the user. If no keypresses are buffered, this
  function will block."
  ([^InputProvider provider]
   (let [key (.readInput provider)]
     {:key (parse-key key) :ctrl (.isCtrlDown key) :alt (.isAltDown key) :shift (.isShiftDown key)})))

(defn get-key-blocking
  "Get the next character from the user. If no keypresses are buffered, this
  function will block."
  ([^InputProvider provider]
   (let [keystroke (get-keystroke-blocking provider)]
    (if (nil? keystroke)
      keystroke
      (:key keystroke)))))
