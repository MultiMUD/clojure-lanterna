(ns lanterna.screen
  (:import com.googlecode.lanterna.screen.Screen)
  (:use [lanterna.common :only [parse-key]])
  (:require [lanterna.constants :as c]
            [lanterna.terminal :as t]))


(defn get-screen
  "Get a screen object.

  kind can be one of the following:

  :auto   - Try to guess the right type of screen based on OS, whether
            there's a windowing environment, etc
  :swing  - Force a Swing-based screen.
  :text   - Force a console (i.e.: non-Swing) screen.  Try to guess the
            appropriate kind of console (UNIX/Cygwin) by the OS.
  :unix   - Force a UNIX console screen.
  :cygwin - Force a Cygwin console screen.

  Options can contain one or more of the following keys:

  :cols    - Width of the desired screen in characters. (default 80)
  :rows    - Height of the desired screen in characters. (default 24)
  :charset - Charset of the desired screen.
             Can be any of (keys lanterna.constants/charsets).
             (default :utf-8)

  NOTE: The options are really just a suggestion!

  The console screen will ignore rows and columns and will be the size of the
  user's window.

  The Swing screen will start out at this size but can be resized later by the
  user, and will ignore the charset entirely.

  God only know what Cygwin will do.

  "
  ([] (get-screen :auto {}))
  ([kind] (get-screen kind {}))
  ([kind {:as opts
          :keys [cols rows charset resize-listener]
          :or {:cols 80
               :rows 24
               :charset :utf-8
               :resize-listener nil}}]
   (new Screen (t/get-terminal kind opts))))


(defn start
  "Start the screen.  Consider using in-screen instead.

  This must be called before you do anything else to the screen.

  "
  [screen]
  (.start screen))

(defn stop
  "Stop the screen.  Consider using in-screen instead.

  This should be called when you're done with the screen.  Don't try to do
  anything else to it after stopping it.

  TODO: Figure out if it's safe to start, stop, and then restart a screen.

  "
  [screen]
  (.start screen))


(defmacro in-screen
  "Start the given screen, perform the body, and stop the screen afterward."
  [screen & body]
  `(do
     (start screen)
     (try ~@body
       (finally (stop screen)))))


(defn redraw
  "Draw the screen.

  This flushes any changes you've made to the actual user-facing terminal.  You
  need to call this for any of your changes to actually show up.

  "
  [screen]
  (.refresh screen))


(defn move-cursor
  "Move the cursor to a specific location on the screen.

  This won't affect where text is printed when you use put-string -- the
  coordinates passed to put-string determine that.

  This is only used to move the cursor, presumably right before a redraw so it
  appears in a specific place.

  "
  [screen]
  (t/move-cursor (::terminal (meta screen))))

(defn put-string
  "Put a string on the screen buffer, ready to be drawn at the next redraw.

  x and y are the column and row to start the string.
  s is the actual string to draw.

  Options can contain any of the following:

  :fg - Foreground color.
  Can be any one of (keys lanterna.constants/colors).
  (default :default)

  :bg - Background color.
  Can be any one of (keys lanterna.constants/colors).
  (default :default)

  :styles - Styles to apply to the text.
  Can be a set containing some/none/all of (keys lanterna.constants/styles).
  (default #{})

  "
  [screen x y s {:as opts
                 :keys [fg bg styles]
                 :or {:fg :default
                      :bg :default
                      :styles #{}}}]
  (let [styles (set (map c/styles styles))]
    (.putString screen x y s
                (c/colors fg)
                (c/colors bg)
                styles)))


(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [screen]
  (parse-key (.readInput screen)))

(defn get-key-blocking
  "Get the next keypress from the user.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, this function will block, checking every
  50ms.  If you want to return nil immediately, use get-key instead.

  TODO: Make the interval configurable.
  TODO: Add a timeout option.

  "
  [screen]
  (let [k (get-key screen)]
    (if (nil? k)
      (do
        (Thread/sleep 50)
        (recur terminal))
      k)))


; (defn get-keys-until
;   "Get a series of keystrokes from the user, stopping when the sentinel is seen.

;   For example, if this function is called like so:

;     (get-keys-until screen :enter)

;   And the user types:

;     Hello world<enter>

;   The following seq will be returned:

;     (\\H \\e \\l \\l \\o \\space \\w \\o \\r \\l \\d)

;   The sentinel is not included in the returned series.

;   If echo is given and is truthy, they typed characters will be written to the
;   screen starting at the current cursor position as they are read.  This can be
;   useful if you're prompting the user for a string of input.

;   "
;   ([screen sentinel] (get-keys-until screen sentinel false))
;   ([screen sentinel echo]
;    (t/get-keys-until (::terminal (meta screen)) echo)))


; (defn add-resize-listener
;   "Create a listener that will call the supplied fn when the screen is resized.

;   The function must take two arguments: the new number of columns and the new
;   number of rows.

;   The listener itself will be returned.  You don't need to do anything with it,
;   but you can use it to remove it later with remove-resize-listener.

;   TODO: Add remove-resize-listener.

;   "
;   [screen listener-fn]
;   (t/add-resize-listener (::terminal (meta screen))
;                          listener-fn))
