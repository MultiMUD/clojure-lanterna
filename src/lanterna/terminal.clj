(ns lanterna.terminal
  (:import com.googlecode.lanterna.TerminalFacade
           com.googlecode.lanterna.terminal.Terminal)
  (:use [lanterna.common :only [parse-key]])
  (:require [lanterna.constants :as c]))


(defn add-resize-listener
  "Create a listener that will call the supplied fn when the terminal is resized.

  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned.  You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener.

  TODO: Add remove-resize-listener.

  "
  [terminal listener-fn]
  (let [listener (reify com.googlecode.lanterna.terminal.Terminal$ResizeListener
                   (onResized [this newSize]
                     (listener-fn (.getColumns newSize)
                                  (.getRows newSize))))]
    (.addResizeListener terminal listener)
    listener))


(defn get-terminal
  "Get a terminal object.

  kind can be one of the following:

  :auto   - Try to guess the right type of terminal based on OS, whether
            there's a windowing environment, etc
  :swing  - Force a Swing-based terminal.
  :text   - Force a console (i.e.: non-Swing) terminal.  Try to guess the
            appropriate kind of console (UNIX/Cygwin) by the OS.
  :unix   - Force a UNIX console terminal.
  :cygwin - Force a Cygwin console terminal.

  Options can contain one or more of the following keys:

  :cols    - Width of the desired terminal in characters (default 80).
  :rows    - Height of the desired terminal in characters (default 24).
  :charset - Charset of the desired terminal.  Can be any of
             (keys lanterna.constants/charsets) (default :utf-8).
  :resize-listener - A function to call when the terminal is resized.  This
                     function should take two parameters: the new number of
                     columns, and the new number of rows.

  NOTE: The options are really just a suggestion!

  The console terminal will ignore rows and columns and will be the size of the
  user's window.

  The Swing terminal will start out at this size but can be resized later by the
  user, and will ignore the charset entirely.

  God only know what Cygwin will do.

  "
  ([] (get-terminal :auto {}))
  ([kind] (get-terminal kind {}))
  ([kind {:as opts
          :keys [cols rows charset resize-listener]
          :or {cols 80
               rows 24
               charset :utf-8
               resize-listener nil}}]
   (let [in System/in
         out System/out
         charset (c/charsets charset)
         terminal (case kind
                    :auto   (TerminalFacade/createTerminal charset)
                    :swing  (TerminalFacade/createSwingTerminal cols rows)
                    :text   (TerminalFacade/createTextTerminal in out charset)
                    :unix   (TerminalFacade/createUnixTerminal in out charset)
                    :cygwin (TerminalFacade/createCygwinTerminal in out charset))]
     (when resize-listener
       (add-resize-listener terminal resize-listener))
     terminal)))


(defn start
  "Start the terminal.  Consider using in-terminal instead."
  [terminal]
  (.enterPrivateMode terminal))

(defn stop
  "Stop the terminal.  Consider using in-terminal instead."
  [terminal]
  (.exitPrivateMode terminal))


(defmacro in-terminal
  "Start the given terminal, perform the body, and stop the terminal afterward."
  [terminal & body]
  `(do
     (start ~terminal)
     (try ~@body
       (finally (stop ~terminal)))))


(defn get-size
  "Return the current size of the terminal as [cols rows]."
  [terminal]
  (let [size (.getTerminalSize terminal)
        cols (.getColumns size)
        rows (.getRows size)]
    [cols rows]))


(defn move-cursor
  "Move the cursor to a specific location on the screen."
  [terminal x y]
  (.moveCursor terminal x y))

(defn put-character
  "Draw the character at the current cursor location.

  If x and y are given, moves the cursor there first.

  Moves the cursor one character to the right, so a sequence of calls will
  output next to each other.

  "
  ([terminal ch]
   (.putCharacter terminal ch))
  ([terminal ch x y]
   (move-cursor terminal x y)
   (put-character terminal ch)))

(defn put-string
  "Draw the string at the current cursor location.

  If x and y are given, moves the cursor there first.

  The cursor will end up at the position directly after the string.

  "
  ([terminal s]
   (dorun (map (partial put-character terminal)
               s)))
  ([terminal s x y]
   (move-cursor terminal x y)
   (put-string terminal s)))


(defn clear
  "Clear the terminal.

  The cursor will be at 0 0 afterwards.

  "
  [terminal]
  (.clearScreen terminal)
  (move-cursor terminal 0 0))


(defn set-fg-color [terminal color]
  (.applyForegroundColor terminal (c/colors color)))

(defn set-bg-color [terminal color]
  (.applyBackgroundColor terminal (c/colors color)))


; TODO: Fix these.
(defn set-style
  "Borked.  Don't use this."
  [terminal style]
  (.applySGR terminal (c/enter-styles style)))

(defn remove-style
  "Borked.  Don't use this."
  [terminal style]
  (.applySGR terminal (c/exit-styles style)))

(defn reset-styles
  "Borked.  Don't use this."
  [terminal]
  (.applySGR terminal c/reset-style))


(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [terminal]
  (parse-key (.readInput terminal)))

(defn get-key-blocking
  "Get the next keypress from the user.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, this function will block, checking every
  50ms.  If you want to return nil immediately, use get-key instead.

  TODO: Make the interval configurable.
  TODO: Add a timeout option.

  "
  [terminal]
  (let [k (get-key terminal)]
    (if (nil? k)
      (do
        (Thread/sleep 50)
        (recur terminal))
      k)))
