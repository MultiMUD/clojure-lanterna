(ns lanterna.input
  (:require [lanterna.constants :as c])
  (:import com.googlecode.lanterna.input.InputProvider
           com.googlecode.lanterna.input.KeyStroke))

(defn parse-key [^KeyStroke k]
  "Return a character or keyword representing the KeyStroke"
  (if (= :character (c/key-codes (.getKeyType k)))
    (.getCharacter k)
    (.getKeyType k)))

(defn get-keystroke
  "Get the next keypress from the user, or nil if none are buffered."
  [^InputProvider screen]
  (let [key (.pollInput screen)]
    (if (nil? key)
      key
      {:key (parse-key key) :ctrl (.isCtrlDown key) :alt (.isAltDown key) :shift (.isShiftDown key)})))

(defn get-key
  "Get the next character from the user sans modifiers, or nil if none are buffered."
  [^InputProvider screen]
  (let [keystroke (get-keystroke screen)]
    (if (nil? keystroke)
      keystroke
      (:key keystroke))))

(defn get-keystroke-blocking
  "Get the next keypress from the user. If no keypresses are buffered, this
  function will block."
  ([^InputProvider screen]
   (let [key (.readInput screen)]
     {:key (parse-key key) :ctrl (.isCtrlDown key) :alt (.isAltDown key) :shift (.isShiftDown key)})))

(defn get-key-blocking
  "Get the next character from the user. If no keypresses are buffered, this
  function will block."
  ([^InputProvider screen]
   (let [keystroke (get-keystroke-blocking screen)]
    (if (nil? keystroke)
      keystroke
      (:key keystroke)))))
