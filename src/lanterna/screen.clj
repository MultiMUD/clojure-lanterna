(ns lanterna.screen
  (:import com.googlecode.lanterna.screen.TerminalScreen)
  (:require [lanterna.constants :as c]
            [lanterna.terminal :as t]
            [lanterna.input :as i]))

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

(defn start!
  "Initialize the screen. This must be called before doing anything else to the
  screen."
  [^TerminalScreen screen]
  (.startScreen screen))

(defn stop!
  "Stop the screen. This should be called when you're done with the screen.
  Don't try to do anything else to it after stopping it.
  TODO: Figure out if it's safe to start, stop, and then restart a screen."
  [^TerminalScreen screen]
  (.stopScreen screen))

(defmacro with-screen
  "Start the given screen, perform the body, and stop the screen afterward."
  [screen & body]
  `(let [screen# ~screen]
     (start screen#)
     (redraw! screen#)
     (try ~@body
       (finally (stop screen#)))))

(defn redraw!
  "Draw the screen, flushing all changes from the back buffer to the front
  buffer. Call this function after making a batch of changes to the back buffer
  to display them."
  [^TerminalScreen screen]
  (.refresh screen))

(defn clear!
  "Clear the screen. This is buffered - redraw the screen to see the effect."
  [^TerminalScreen screen]
  (.clear screen))
