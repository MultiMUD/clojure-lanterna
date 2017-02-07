(ns lanterna.api
  (:refer-clojure :exclude [flush])
  (:require
    [lanterna.protocols :as prot]
    [lanterna.constants :as c])
  (:import 
    [com.googlecode.lanterna.input KeyStroke]
    [lanterna.protocols Input Output]))


(defn put-string
  "Puts string at the current cursor position with the
  current style (foreground & background colors, styles)
  on the output. Does not cope well with control
  characters in the string (including newline).
  Returns self."
  [^Output self string]
   self)

(defn put-char
  "Puts character at the current cursor position with the
  current style (foreground & background colors, styles)
  on the output. Does not cope well with ch being
  a control character (including newline).
  Returns self."
  [^Output self ch]
  self)

(defn put
  "puts the given thing (a character, a string, or a collection
  thereof) starting at the current cursor position on the output. 
  Does cope well with control characters, multiple lines etc.
  Returns self."
  [^Output self single-thing]
  self)

(defn put-sheet
  "generically support the experimental `sheet' API based on the
  Output protocol. Returns self."
  [^Output self sheet]
  self)

(defn- parse-key 
  "Return a character or keyword representing the KeyStroke"
  [^KeyStroke k]
  (if (= :normal (c/key-codes (.getKeyType k)))
    (.getCharacter k)
    (c/key-codes (.getKeyType k))))

(defn ^Character poll-char 
    "returns (immediately) a single character from 
    the input buffer, or nil if it is empty."
    [^Input this]
  (if-let [stroke (prot/-poll-stroke this)]
    (parse-key stroke)))

(defn ^Character get-char 
    "returns a single character from the input.
    Blocks if there is no input available."
    [^Input this]
  (if-let [stroke (prot/-get-stroke this)]
    (parse-key stroke)))

;;; FIXME: implement
;;; Do we keep the timeout on this one?
(defn get-string 
    "returns a string from the input which is terminated by the
    given eos (end of string, a regular expression pattern).
    Keeps consuming (and potentially blocking) input until the
    terminator is encountered. If the timeout finishes before
    the terminator has been encountered, the read string so far
    is returned, and _not_ put back into the input buffer.
    Blocks for timeout unit (default: indefinitely) if there is
    no input available."
  [^Input self eos {:as opts :keys [timeout unit] :or {timeout -1}}]
  nil)
