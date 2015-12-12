(ns lanterna.terminal
  (:import com.googlecode.lanterna.TerminalFacade
           com.googlecode.lanterna.terminal.Terminal
           com.googlecode.lanterna.terminal.swing.SwingTerminal
           com.googlecode.lanterna.terminal.swing.TerminalAppearance
           com.googlecode.lanterna.terminal.swing.TerminalPalette
           java.awt.GraphicsEnvironment
           java.awt.Font)
  (:use [lanterna.common :only [parse-key block-on]])
  (:require [lanterna.constants :as c]))



(defn add-resize-listener
  "Create a listener that will call the supplied fn when the terminal is resized.

  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned.  You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener.

  "
  [^Terminal terminal listener-fn]
  (let [listener (reify com.googlecode.lanterna.terminal.Terminal$ResizeListener
                   (onResized [this newSize]
                     (listener-fn (.getColumns newSize)
                                  (.getRows newSize))))]
    (.addResizeListener terminal listener)
    listener))

(defn remove-resize-listener
  "Remove a resize listener from the given terminal."
  [^Terminal terminal listener]
  (.removeResizeListener terminal listener))

(defn get-available-fonts []
  (set (.getAvailableFontFamilyNames
         (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(defn- get-font-name [font]
  (let [fonts (if (coll? font) font [font])
        fonts (concat fonts ["Monospaced"])
        available (get-available-fonts)]
    (first (filter available fonts))))


(defn- get-swing-terminal [cols rows
                           {:as opts
                            :keys [font font-size palette]
                            :or {font ["Menlo" "Consolas" "Monospaced"]
                                 font-size 14
                                 palette :mac-os-x}}]
  (let [font (get-font-name font)
        appearance (new TerminalAppearance
                        (new Font font Font/PLAIN font-size)
                        (new Font font Font/BOLD font-size)
                        (c/palettes palette) true)]
    (new SwingTerminal appearance cols rows)))

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
  :font      - Font to use.  String or sequence of strings.
               Use (lanterna.terminal/get-available-fonts) to see your options.
               Will fall back to a basic monospaced font if none of the given
               names are available.
  :font-size - Font size (default 14).
  :palette   - Color palette to use. Can be any of
               (keys lanterna.constants/palettes) (default :mac-os-x).

  NOTE: The options are really just a suggestion!

  The console terminal will ignore rows and columns and fonts and colors.

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
                    :swing  (get-swing-terminal cols rows opts)
                    :text   (TerminalFacade/createTextTerminal in out charset)
                    :unix   (TerminalFacade/createUnixTerminal in out charset)
                    :cygwin (TerminalFacade/createCygwinTerminal in out charset))]
     (when resize-listener
       (add-resize-listener terminal resize-listener))
     terminal)))


(defn start
  "Start the terminal.  Consider using in-terminal instead."
  [^Terminal terminal]
  (.enterPrivateMode terminal))

(defn stop
  "Stop the terminal.  Consider using in-terminal instead."
  [^Terminal terminal]
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
  [^Terminal terminal]
  (let [size (.getTerminalSize terminal)
        cols (.getColumns size)
        rows (.getRows size)]
    [cols rows]))


(defn move-cursor
  "Move the cursor to a specific location on the screen."
  [^Terminal terminal x y]
  (.moveCursor terminal x y))

(defn put-character
  "Draw the character at the current cursor location.

  If x and y are given, moves the cursor there first.

  Moves the cursor one character to the right, so a sequence of calls will
  output next to each other.

  "
  ([^Terminal terminal ch]
   (.putCharacter terminal ch))
  ([^Terminal terminal ch x y]
   (move-cursor terminal x y)
   (put-character terminal ch)))

(defn put-string
  "Draw the string at the current cursor location.

  If x and y are given, moves the cursor there first.

  The cursor will end up at the position directly after the string.

  "
  ([^Terminal terminal s]
   (dorun (map (partial put-character terminal)
               s)))
  ([terminal s x y]
   (move-cursor terminal x y)
   (put-string terminal s)))


(defn clear
  "Clear the terminal.

  The cursor will be at 0 0 afterwards.

  "
  [^Terminal terminal]
  (.clearScreen terminal)
  (move-cursor terminal 0 0))


(defn set-fg-color [^Terminal terminal color]
  (.applyForegroundColor terminal (c/colors color)))

(defn set-bg-color [^Terminal terminal color]
  (.applyBackgroundColor terminal (c/colors color)))

(defn set-style
  "Enter a style"
  [^Terminal terminal style]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [(c/enter-styles style)])))

(defn remove-style
  "Exit a style"
  [^Terminal terminal style]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [(c/exit-styles style)])))

(defn reset-styles
  "Reset all styles"
  [^Terminal terminal]
  (.applySGR terminal (into-array com.googlecode.lanterna.terminal.Terminal$SGR [c/reset-style])))


(defn get-key
  "Get the next keypress from the user, or nil if none are buffered.

  If the user has pressed a key, that key will be returned (and popped off the
  buffer of input).

  If the user has *not* pressed a key, nil will be returned immediately.  If you
  want to wait for user input, use get-key-blocking instead.

  "
  [^Terminal terminal]
  (parse-key (.readInput terminal)))

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
  ([^Terminal terminal] (get-key-blocking terminal {}))
  ([^Terminal terminal {:keys [interval timeout] :as opts}]
     (block-on get-key [terminal] opts)))


(comment

  (def t (get-terminal :swing
                       {:cols 40 :rows 30
                        :font ["Menlo"]
                        :font-size 24
                        :palette :gnome}))
  (start t)
  (set-fg-color t :yellow)
  (put-string t "Hello, world!")
  (get-key-blocking t {:timeout 1000})
  (get-key-blocking t {:interval 2000})
  (stop t)

  )
