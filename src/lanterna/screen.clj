(ns lanterna.screen
  (:import com.googlecode.lanterna.screen.TerminalScreen)
  (:require [lanterna.constants :as c]
            [lanterna.terminal :as t]
            [lanterna.input :as i]))

(defn enumerate [s]
  (map vector (iterate inc 0) s))

(defn add-resize-listener
  "Create a listener that will call the supplied fn when the screen is resized.

  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned. You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener.

  "
  [^TerminalScreen screen listener-fn]
  (t/add-resize-listener (.getTerminal screen)
                         listener-fn))

(defn remove-resize-listener
  "Remove a resize listener from the given screen."
  [^TerminalScreen screen listener]
  (t/remove-resize-listener (.getTerminal screen) listener))

(defn get-screen
  "Get a screen object.

  kind can be one of the following:

  :auto   - Use a Swing terminal if a windowing system is present, or use a text
            based terminal appropriate to the operating system.
  :text   - Force a text-based (i.e. non-Swing) terminal. Try to guess the
            appropriate kind of terminal (UNIX/Cygwin) by the OS.
  :unix   - Force a UNIX console terminal.
  :cygwin - Force a Cygwin console terminal.

  Options can contain one or more of the following keys:

  :title   - The name of the terminal window (default \"terminal\")
  :cols    - Width of the desired terminal in characters (default 80).
  :rows    - Height of the desired terminal in characters (default 24).
  :charset - Charset of the desired terminal. Can be any of
             (keys lanterna.constants/charsets) (default :utf-8).
  :resize-listener - A function to call when the terminal is resized. This
                     function should take two parameters: the new number of
                     columns, and the new number of rows.
  :font      - Font to use. String or collection of strings.
               Use (lanterna.terminal/get-available-fonts) to see your options.
               Will fall back to a basic monospaced font if none of the given
               names are available.
  :font-size - Font size (default 14).
  :palette   - Color palette to use. Can be any of
               (keys lanterna.constants/palettes) (default :mac-os-x).
  :auto   - Try to guess the right type of screen based on OS, whether
            there's a windowing environment, etc
  :swing  - Force a Swing-based screen.
  :text   - Force a console (i.e.: non-Swing) screen. Try to guess the
            appropriate kind of console (UNIX/Cygwin) by the OS.
  :unix   - Force a UNIX console screen.
  :cygwin - Force a Cygwin console screen.

  NOTE: The options are really just a suggestion!

  The console screen will ignore rows and columns and will be the size of the
  user's window.

  The Swing screen will start out at this size but can be resized later by the
  user, and will ignore the charset entirely."
  ([] (get-screen {}))
  ([{:as opts
     :keys [kind title cols rows charset resize-listener font font-size palette]
     :or {kind :auto
          title "terminal"
          cols 80
          rows 24
          charset :utf-8
          resize-listener nil
          font ["Droid Sans Mono" "DejaVu Sans Mono" "Consolas" "Monospaced" "Mono"]
          font-size 14
          palette :mac-os-x}}]
   (TerminalScreen. (t/get-terminal opts))))

(defn start
  "Initialize the screen. This must be called before doing anything else to the
  screen."
  [^TerminalScreen screen]
  (.startScreen screen))

(defn stop
  "Stop the screen. This should be called when you're done with the screen.
  Don't try to do anything else to it after stopping it.
  TODO: Figure out if it's safe to start, stop, and then restart a screen."
  [^TerminalScreen screen]
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
  [^TerminalScreen screen]
  (let [size (.getTerminalSize (.getTerminal screen))]
    [(.getColumns size) (.getRows size)]))

(defn redraw
  "Draw the screen, flushing all changes from the back buffer to the front
  buffer. Call this function after making a batch of changes to the back buffer
  to display them."
  [^TerminalScreen screen]
  (.refresh screen))

(defn move-cursor
  "Move the cursor to a specific location on the screen."
  ([^TerminalScreen screen x y]
   (t/move-cursor (.getTerminal screen) x y))
  ([^TerminalScreen screen [x y]]
   (t/move-cursor (.getTerminal screen) x y)))

(defn get-cursor
  "Return the cursor position as [x y]."
  [^TerminalScreen screen]
  (t/get-cursor (.getTerminal screen)))

(defn put-char
  "Draw the character at the current cursor location. If x and y are given,
  moves the cursor there first. Moves the cursor one character to the right, so
  a sequence of calls will output next to each other."
  ([^TerminalScreen screen]
   (put-char (.getTerminal screen ch)))
  ([^TerminalScreen screen ^Character ch ^Integer x ^Integer y]
   (put-char terminal ch x y)))

(defn put-string
  "Put a string on the screen buffer, ready to be drawn at the next redraw.

  x and y are the column and row to start the string.
  s is the actual string to draw.

  Options can contain any of the following:

  :fg - Foreground color. Can be any one of (keys lanterna.constants/colors).
        (default :default)
  :bg - Background color. Can be any one of (keys lanterna.constants/colors).
        (default :default)
  :styles - Styles to apply to the text. Can be a set containing some/none/all
            of (keys lanterna.constants/styles). (default #{})"
  ([^TerminalScreen screen ^String s]
   (t/put-string (.getTerminal screen) s))
  ([^TerminalScreen screen ^String s ^Integer x ^Integer y]
   (t/put-string (.getTerminal screen) s x y))
  ([^TerminalScreen screen ^String s ^Integer x ^Integer y
    {:as opts
     :keys [fg bg styles]
     :or {fg :default
          bg :default
          styles #{}}}]
   (t/put-string (.getTerminal screen) s x y opts)))

(defn put-sheet
  "EXPERIMENTAL!  Turn back now!

  Draw a sheet to the screen (buffered, of course).

  A sheet is a two-dimentional sequence of things to print to the screen. It
  will be printed with its upper-left corner at the given x and y coordinates.

  Sheets can take several forms. The simplest sheet is a vector of strings:

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
  "Clear the screen. This is buffered - redraw the screen to see the effect."
  [^TerminalScreen screen]
  (.clear screen))

(def get-keystroke i/get-keystroke)
(def get-key i/get-key)
(def get-keystroke-blocking i/get-keystroke-blocking)
(def get-key-blocking i/get-keystroke-blocking)
