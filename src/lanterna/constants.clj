(ns lanterna.constants
  (:import java.nio.charset.Charset
           com.googlecode.lanterna.TerminalFacade
           com.googlecode.lanterna.screen.Screen
           com.googlecode.lanterna.terminal.Terminal
           com.googlecode.lanterna.screen.ScreenCharacterStyle
           com.googlecode.lanterna.terminal.swing.TerminalPalette
           com.googlecode.lanterna.input.Key))


(def charsets {:utf-8 (Charset/forName "UTF-8")})

(def colors
  {:black   com.googlecode.lanterna.terminal.Terminal$Color/BLACK
   :white   com.googlecode.lanterna.terminal.Terminal$Color/WHITE
   :red     com.googlecode.lanterna.terminal.Terminal$Color/RED
   :green   com.googlecode.lanterna.terminal.Terminal$Color/GREEN
   :blue    com.googlecode.lanterna.terminal.Terminal$Color/BLUE
   :cyan    com.googlecode.lanterna.terminal.Terminal$Color/CYAN
   :magenta com.googlecode.lanterna.terminal.Terminal$Color/MAGENTA
   :yellow  com.googlecode.lanterna.terminal.Terminal$Color/YELLOW
   :default com.googlecode.lanterna.terminal.Terminal$Color/DEFAULT})

(def styles
  {:bold ScreenCharacterStyle/Bold
   :reverse ScreenCharacterStyle/Reverse
   :underline ScreenCharacterStyle/Underline
   :blinking ScreenCharacterStyle/Blinking})

(def key-codes
  {com.googlecode.lanterna.input.Key$Kind/NormalKey :normal
   com.googlecode.lanterna.input.Key$Kind/Escape :escape
   com.googlecode.lanterna.input.Key$Kind/Backspace :backspace
   com.googlecode.lanterna.input.Key$Kind/ArrowLeft :left
   com.googlecode.lanterna.input.Key$Kind/ArrowRight :right
   com.googlecode.lanterna.input.Key$Kind/ArrowUp :up
   com.googlecode.lanterna.input.Key$Kind/ArrowDown :down
   com.googlecode.lanterna.input.Key$Kind/Insert :insert
   com.googlecode.lanterna.input.Key$Kind/Delete :delete
   com.googlecode.lanterna.input.Key$Kind/Home :home
   com.googlecode.lanterna.input.Key$Kind/End :end
   com.googlecode.lanterna.input.Key$Kind/PageUp :page-up
   com.googlecode.lanterna.input.Key$Kind/PageDown :page-down
   com.googlecode.lanterna.input.Key$Kind/Tab :tab
   com.googlecode.lanterna.input.Key$Kind/ReverseTab :reverse-tab
   com.googlecode.lanterna.input.Key$Kind/Enter :enter
   com.googlecode.lanterna.input.Key$Kind/Unknown :unknown
   com.googlecode.lanterna.input.Key$Kind/CursorLocation :cursor-location})


(def palettes
  {:gnome      TerminalPalette/GNOME_TERMINAL
   :vga        TerminalPalette/STANDARD_VGA
   :windows-xp TerminalPalette/WINDOWS_XP_COMMAND_PROMPT
   :mac-os-x   TerminalPalette/MAC_OS_X_TERMINAL_APP
   :xterm      TerminalPalette/PUTTY
   :putty      TerminalPalette/XTERM})

(def enter-styles
  {:bold com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_BOLD
   :reverse com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_REVERSE
   :blinking com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_BLINK
   :underline com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_UNDERLINE})

(def exit-styles
  {:bold com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_BOLD
   :reverse com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_REVERSE
   :blinking com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_BLINK
   :underline com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_UNDERLINE})

(def reset-style
  com.googlecode.lanterna.terminal.Terminal$SGR/RESET_ALL)
