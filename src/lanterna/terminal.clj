(ns lanterna.terminal
  (:import com.googlecode.lanterna.TerminalSize
           com.googlecode.lanterna.terminal.Terminal
           com.googlecode.lanterna.terminal.DefaultTerminalFactory
           com.googlecode.lanterna.terminal.SimpleTerminalResizeListener
           com.googlecode.lanterna.terminal.ansi.UnixTerminal
           com.googlecode.lanterna.terminal.ansi.CygwinTerminal
           com.googlecode.lanterna.terminal.swing.SwingTerminal
           com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
           com.googlecode.lanterna.terminal.swing.TerminalEmulatorColorConfiguration
           com.googlecode.lanterna.terminal.swing.TerminalEmulatorDeviceConfiguration
           com.googlecode.lanterna.terminal.swing.AWTTerminalFontConfiguration$BoldMode
           com.googlecode.lanterna.screen.TerminalScreen
           java.awt.GraphicsEnvironment
           java.awt.Font)
  (:require [lanterna.constants :as c]
            [lanterna.input :as i]))

(defn underlying-term
  "Get the underlying terminal of an object"
  [term-or-screen]
  (if (instance? Terminal term-or-screen)
    term-or-screen
    (.getTerminal term-or-screen)))

(defn set-fg!
  [terminal color]
  (let [term (underlying-term terminal)]
    (.setForegroundColor term (c/colors color))))

(defn set-bg!
  [terminal color]
  (let [term (underlying-term terminal)]
    (.setBackgroundColor term (c/colors color))))

(defn set-style!
  "Enter a style"
  [terminal style]
  (let [term (underlying-term terminal)]
    (.enableSGR term (c/styles style))))

(defn remove-style!
  "Exit a style"
  [terminal style]
  (let [term (underlying-term terminal)]
    (.disableSGR term (c/styles style))))

(defn reset-styles!
  "Reset all styles and return colors to their defaults"
  [terminal]
  (let [term (underlying-term terminal)]
    (.resetColorAndSGR term)))

(defmacro with-styles
  "Perform body with the given colors and styles."
  [term & body]
  `(try ~@body
        (finally
          (reset-styles! ~term))))

(defn move-cursor!
  "Move the cursor to a specific location on the screen."
  ([terminal [x y]]
   (move-cursor! terminal x y))
  ([terminal x y]
   (let [term (underlying-term terminal)]
     (.setCursorPosition term x y))))

(defn get-cursor
  "Return the cursor position as (x, y)."
  [terminal]
  (let [term (underlying-term terminal)
        pos (.getCursorPosition terminal)]
    [(.getColumn pos) (.getRow pos)]))

(defmacro with-pos
  "Perform body with the given colors and styles."
  [term & body]
  `(let [[ox# oy#] (get-cursor ~term)]
     (try ~@body
          (finally
            (move-cursor! ~term ox# oy#)))))

(defn add-resize-listener!
  "Create a listener that will call the supplied fn when the terminal is resized.
  The function must take two arguments: the new number of columns and the new
  number of rows.

  The listener itself will be returned. You don't need to do anything with it,
  but you can use it to remove it later with remove-resize-listener."
  [terminal listener-fn]
  (let [term (underlying-term terminal)
        listener (proxy [SimpleTerminalResizeListener] [(.getTerminalSize term)]
                   (onResized [term newSize]
                     (listener-fn
                      term
                      (.getColumns newSize)
                      (.getRows newSize))))]
    (.addResizeListener term listener)
    listener))

(defn remove-resize-listener!
  "Remove a resize listener from the given term."
  [terminal listener]
  (let [term (underlying-term terminal)]
    (.removeResizeListener term listener)))

(defn get-available-fonts []
  (set (.getAvailableFontFamilyNames
        (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(defn- get-font [fonts]
  (let [available (get-available-fonts)]
    (first (filter available fonts))))

(defn- get-factory [kind
                    {:keys [title cols rows charset font font-size palette]
                     :or {title "terminal"
                          cols 80
                          rows 24
                          charset :utf-8
                          font ["Droid Sans Mono" "DejaVu Sans Mono" "Consolas" "Monospaced" "Mono"]
                          font-size 14
                          palette :mac-os-x}}]
  (doto (DefaultTerminalFactory. System/out System/in (c/charsets charset))
    (.setForceTextTerminal (= :text kind))
    (.setTerminalEmulatorTitle title)
    (.setTerminalEmulatorFontConfiguration
     (SwingTerminalFontConfiguration.
      true
      AWTTerminalFontConfiguration$BoldMode/EVERYTHING
      (into-array [(Font. (get-font font) Font/PLAIN font-size)])))
    (.setInitialTerminalSize (TerminalSize. cols rows))
    (.setTerminalEmulatorColorConfiguration (TerminalEmulatorColorConfiguration/newInstance (c/palettes palette)))
    (.setTerminalEmulatorDeviceConfiguration (TerminalEmulatorDeviceConfiguration.)))) ; TODO: Allow customization


(defn get-terminal
  "Get a terminal object.

  kind can be one of the following:

  :auto   - Use a Swing terminal if a windowing system is present, or use a text
            based terminal appropriate to the operating system.
  :text   - Force a text-based (i.e. non-Swing) terminal. Try to guess the
            appropriate kind of terminal (UNIX/Cygwin) by the OS.
  :unix   - Force a UNIX text-based terminal.
  :cygwin - Force a Cygwin text-based terminal.

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

  The console terminal will ignore rows and columns and fonts and colors.

  The Swing terminal will start out at this size but can be resized later by the
  user, and will ignore the charset entirely."
  ([] (get-terminal {}))
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
          palette :gnome}}]
   (let [fonts (if (coll? font) font [font])
         factory (get-factory kind opts)
         terminal (case kind
                    :auto (.createTerminal factory)
                    :text (.createTerminal factory)
                    :unix (UnixTerminal. System/in System/out (c/charsets charset))
                    :cygwin (CygwinTerminal. System/in System/out (c/charsets charset)))]
     (when resize-listener
       (add-resize-listener! terminal resize-listener))
     terminal)))

(defn start!
  "Start the terminal. Consider using with-terminal instead."
  [terminal]
  (let [term (underlying-term terminal)]
    (.enterPrivateMode terminal)))

(defn stop!
  "Stop the terminal. Consider using with-terminal instead."
  [terminal]
  (let [term (underlying-term terminal)]
    (.exitPrivateMode term)))

(defmacro with-terminal
  "Start the given terminal, perform the body, and stop the terminal afterward."
  [terminal & body]
  `(do
     (start ~terminal)
     (try ~@body
          (finally (stop ~terminal)))))

(defn get-size
  "Return the current size of the terminal as [cols rows]."
  [terminal]
  (let [term (underlying-term terminal)
        size (.getTerminalSize terminal)]
    [(.getColumns size) (.getRows size)]))

(defn put-char!
  "Draw the character at the current cursor location. If x and y are given,
  moves the cursor there first. Moves the cursor one character to the right, so
  a sequence of calls will output next to each other."
  ([terminal ch]
   (let [term (underlying-term terminal)]
     (.putCharacter term ch)))
  ([terminal ch x y]
   (with-pos terminal
     (move-cursor! terminal x y)
     (put-char! terminal ch)))
  ([terminal ch x y
    {:keys [fg bg styles]
     :or {fg :default
          bg :default
          styles #{}}}]
   (with-styles terminal
     (doseq [style styles]
       (set-style! terminal style))
     (set-fg! terminal fg)
     (set-bg! terminal bg)
     (put-char! terminal ch x y))))

(defn put-string!
  "Draw the string at the current cursor location. If x and y are given, moves
  the cursor there first. The cursor will end up at the position directly after
  the string."
  ([terminal s]
   (doseq [c s] (put-char! terminal c)))
  ([terminal ^String s ^Integer x ^Integer y]
   (with-pos terminal
     (move-cursor! terminal x y)
     (put-string! terminal s)))
  ([terminal ^String s ^Integer x ^Integer y
    {:as opts
     :keys [fg bg styles]
     :or {fg :default
          bg :default
          styles #{}}}]
   (with-styles terminal
     (doseq [style styles]
       (set-style! terminal style))
     (set-fg! terminal fg)
     (set-bg! terminal bg)
     (put-string! terminal s x y))))

(defn clear!
  "Clear the terminal"
  [^Terminal terminal]
  (.clearScreen terminal))
