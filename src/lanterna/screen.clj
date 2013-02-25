(ns lanterna.screen
  (:import com.googlecode.lanterna.screen.Screen
           com.googlecode.lanterna.terminal.Terminal)
  (:use [lanterna.common :only [parse-key block-on]])
  (:require [lanterna.constants :as c]
            [lanterna.terminal :as t]))


(defn enumerate [s]
  (map vector (iterate inc 0) s))

(defn add-resize-listener
  "Create a listener that will call the supplied fn when the screen is resized.

  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned.  You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener.

  "
  [^Screen screen listener-fn]
  (t/add-resize-listener (.getTerminal screen)
                         listener-fn))

(defn remove-resize-listener
  "Remove a resize listener from the given screen."
  [^Screen screen listener]
  (t/remove-resize-listener (.getTerminal screen) listener))

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

  :cols    - Width of the desired screen in characters (default 80).
  :rows    - Height of the desired screen in characters (default 24).
  :charset - Charset of the desired screen.  Can be any of
             (keys lanterna.constants/charsets) (default :utf-8).
  :resize-listener - A function to call when the screen is resized.  This
                     function should take two parameters: the new number of
                     columns, and the new number of rows.
  :font      - Font to use.  String or sequence of strings.
               Use (lanterna.terminal/get-available-fonts) to see your options.
               Will fall back to a basic monospaced font if none of the given
               names are available.
  :font-size - An int of the size of the font to use.

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
  [^Screen screen]
  (.startScreen screen))

(defn stop
  "Stop the screen.  Consider using in-screen instead.

  This should be called when you're done with the screen.  Don't try to do
  anything else to it after stopping it.

  TODO: Figure out if it's safe to start, stop, and then restart a screen.

  "
  [^Screen screen]
  (.stopScreen screen))


(defmacro in-screen
  "Start the given screen, perform the body, and stop the screen afterward."
  [screen & body]
  `(let [screen# ~screen]
     (start screen#)
     (try ~@body
       (finally (stop screen#)))))


(defn get-size
  "Return the current size of the screen as [cols rows]."
  [^Screen screen]
  (let [size (.getTerminalSize screen)
        cols (.getColumns size)
        rows (.getRows size)]
    [cols rows]))


(defn redraw
  "Draw the screen.

  This flushes any changes you've made to the actual user-facing terminal.  You
  need to call this for any of your changes to actually show up.

  "
  [^Screen screen]
  (.refresh screen))


(defn move-cursor
  "Move the cursor to a specific location on the screen.

  This won't affect where text is printed when you use put-string -- the
  coordinates passed to put-string determine that.

  This is only used to move the cursor, presumably right before a redraw so it
  appears in a specific place.

  "
  [^Screen screen x y]
  (.setCursorPosition screen x y))

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
  ([^Screen screen x y s] (put-string screen x y s {}))
  ([^Screen screen x y ^String s {:as opts
                                  :keys [fg bg styles]
                                  :or {fg :default
                                       bg :default
                                       styles #{}}}]
   (let [styles ^clojure.lang.PersistentHashSet (set (map c/styles styles))
         x (int x)
         y (int y)
         fg ^com.googlecode.lanterna.terminal.Terminal$Color (c/colors fg)
         bg ^com.googlecode.lanterna.terminal.Terminal$Color (c/colors bg)]
     (.putString screen x y s fg bg styles))))

(defn put-sheet
  "EXPERIMENTAL!  Turn back now!

  Draw a sheet to the screen (buffered, of course).

  A sheet is a two-dimentional sequence of things to print to the screen.  It
  will be printed with its upper-left corner at the given x and y coordinates.

  Sheets can take several forms.  The simplest sheet is a vector of strings:

    (put-sheet scr 2 0 [\"foo\" \"bar\" \"hello!\"])

  This would print something like

     0123456789
    0  foo
    1  bar
    2  hello!

  As you can see, the rows of a sheet do not need to all be the same size.
  Shorter rows will *not* be padded in any way.

  Rows can also be sequences themselves, of characters or strings:

    (put-sheet scr 5 0 [[\\s \\p \\a \\m] [\"e\" \"g\" \"g\" \"s\"]])

     0123456789
    0     spam
    1     eggs

  Finally, instead of single characters of strings, you can pass a vector of a
  [char-or-string options-map], like so:

    (put-sheet scr 1 0 [[[\\r {:fg :red}] [\\g {:fg :green}]]
                        [[\\b {:fg :blue}]]])

     0123456789
    0 rg
    1 b

  And the letters would be colored appropriately.

  Finally, you can mix and match any and all of these within a single sheet or
  row:

    (put-sheet scr 2 0 [\"foo\"
                        [\"b\" \\a [\\r {:bg :yellow :fg :black}])

  "
  [screen x y sheet]
  (letfn [(put-item [c r item]
            (cond
              (string? item) (put-string screen c r item)
              (char? item)   (put-string screen c r (str item))
              (vector? item) (let [[i opts] item]
                               (if (char? i)
                                 (put-string screen c r (str i) opts)
                                 (put-string screen c r i opts)))
              :else nil ; TODO: die loudly
              ))
          (put-row [r row]
            (doseq [[c item] (enumerate row)]
              (put-item (+ x c) r item)))]
    (doseq [[i row] (enumerate sheet)]
      (if (string? row)
        (put-string screen x (+ y i) row)
        (put-row (+ y i) row)))))

(defn clear
  "Clear the screen.

  Note that this is buffered like everything else, so you'll need to redraw
  the screen to see the effect.

  "
  [^Screen screen]
  (.clear screen))


(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [^Screen screen]
  (parse-key (.readInput screen)))

(defn get-key-blocking
  "Get the next keypress from the user.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, this function will block, checking every
  50ms.  If you want to return nil immediately, use get-key instead.

  Options can include any of the following keys:

  :interval - sets the interval between checks
  :timeout  - sets the maximum amount of time blocking will occur before
              returning nil

  "
  ([^Screen screen] (get-key-blocking screen {}))
  ([^Screen screen {:keys [interval timeout] :as opts}]
     (block-on get-key [screen] opts)))


(comment
  (def s (get-screen))
  (start s)

  (put-sheet s 5 5 ["foo" "bar" "hello"])
  (put-sheet s 5 9 [[\f \o \o] ["b" "a" "r"] "hello"])
  (let [r [\r {:bg :red :fg :white}]
        g [\g {:bg :green :fg :black}]
        b [\b {:bg :blue :fg :white}]]
    (put-sheet s 5 13 [[r r r] [g g g] [b b b]]))

  (redraw s)
  (stop s)
  )
