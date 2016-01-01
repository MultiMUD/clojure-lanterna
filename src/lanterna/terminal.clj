(ns lanterna.terminal
  (:import com.googlecode.lanterna.terminal.Terminal
           com.googlecode.lanterna.TerminalSize
           com.googlecode.lanterna.terminal.swing.SwingTerminal
           com.googlecode.lanterna.terminal.swing.SwingTerminalDeviceConfiguration
           com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
           com.googlecode.lanterna.terminal.swing.SwingTerminalColorConfiguration
           com.googlecode.lanterna.terminal.DefaultTerminalFactory
           com.googlecode.lanterna.TextCharacter
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
  (let [listener (reify com.googlecode.lanterna.terminal.ResizeListener
                   (onResized [this _ newSize]
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
        size (new TerminalSize cols rows)]
    (new SwingTerminal size
                       (new SwingTerminalDeviceConfiguration)
                       (SwingTerminalFontConfiguration/newInstance font)
                       (SwingTerminalColorConfiguration/newInstance (c/palettes palette)))))

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
         factory (new DefaultTerminalFactory out in charset)
         terminal (case kind
                    :auto   (.createTerminal factory)
                    :swing  (get-swing-terminal cols rows opts))]
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

(defn get-cursor-position
  "Return the current cursor position on the terminal as [col row]"
  [^Terminal terminal]
  (let [pos (.getCursorPosition terminal)
        col (.getColumn pos)
        row (.getRow pos)]
    [col row]))

(defn move-cursor
  "Move the cursor to a specific location on the screen."
  [^Terminal terminal x y]
  (.moveCursor terminal x y))

(defn set-character [^Terminal terminal x y ch]
  "Draw the character at the given cursor location."
   (.set-character terminal x y (new TextCharacter ch)))

(defn put-string [^Terminal terminal x y s]
  "Draw the string at the given cursor location.

  The cursor will end up at the position directly after the string.
  "
  (doseq [i (range 0 (.length s))]
    (set-character terminal (+ x i) y) (.charAt s i))
  (move-cursor terminal (+ x (.length s)) y))

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
  (stop t))
