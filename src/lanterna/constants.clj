(ns lanterna.constants
  (:import java.nio.charset.Charset
           com.googlecode.lanterna.TextColor$ANSI
           com.googlecode.lanterna.terminal.swing.TerminalEmulatorPalette
           com.googlecode.lanterna.input.KeyType
           com.googlecode.lanterna.SGR))


(def charsets {:utf-8 (Charset/forName "UTF-8")})

(def colors
  {:black   TextColor$ANSI/BLACK
   :white   TextColor$ANSI/WHITE
   :red     TextColor$ANSI/RED
   :green   TextColor$ANSI/GREEN
   :blue    TextColor$ANSI/BLUE
   :cyan    TextColor$ANSI/CYAN
   :magenta TextColor$ANSI/MAGENTA
   :yellow  TextColor$ANSI/YELLOW
   :default TextColor$ANSI/DEFAULT})

(def key-codes
  {KeyType/Character :normal
   KeyType/Escape :escape
   KeyType/Backspace :backspace
   KeyType/ArrowLeft :left
   KeyType/ArrowRight :right
   KeyType/ArrowUp :up
   KeyType/ArrowDown :down
   KeyType/Insert :insert
   KeyType/Delete :delete
   KeyType/Home :home
   KeyType/End :end
   KeyType/PageUp :page-up
   KeyType/PageDown :page-down
   KeyType/Tab :tab
   KeyType/ReverseTab :reverse-tab
   KeyType/Enter :enter
   KeyType/Unknown :unknown
   KeyType/CursorLocation :cursor-location
   KeyType/EOF :eof})

(def palettes
  {:gnome      TerminalEmulatorPalette/GNOME_TERMINAL
   :vga        TerminalEmulatorPalette/STANDARD_VGA
   :windows-xp TerminalEmulatorPalette/WINDOWS_XP_COMMAND_PROMPT
   :mac-os-x   TerminalEmulatorPalette/MAC_OS_X_TERMINAL_APP
   :putty      TerminalEmulatorPalette/PUTTY
   :xterm      TerminalEmulatorPalette/XTERM})

(def styles
  {:bold SGR/BOLD
   :reverse SGR/REVERSE
   :blinking SGR/BLINK
   :underline SGR/UNDERLINE
   :circled SGR/CIRCLED
   :strikethrough SGR/CROSSED_OUT
   :fraktur SGR/FRAKTUR})
