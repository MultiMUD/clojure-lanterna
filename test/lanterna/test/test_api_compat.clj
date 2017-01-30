(ns lanterna.test.test-api-compat
  (:require 
    [lanterna.constants :as const]
    [lanterna.input :as input]
    [lanterna.terminal :as terminal]
    [lanterna.screen :as screen])
  (:use [clojure.test]))

;;; API Compat test suite
;;;
;;; One of the requirements as communicated by the original developer (steve losh)
;;; was to maintain a stable API. Ideally, one would automatically notice if the API
;;; changes. Well, cannot test that without a definition set of what encompasses the API.
;;; Once (semver) >= 1.0.0 is achieved, this becomes a requirement for keeping the same
;;; major version number.
;;;
;;; The heritage of the API definition is reflected in the symbol naming
;;; (leading version indicator with dots replaced by dashes)

;;;; lanterna.constants
;;;
;;; these are the keys we expect to be found in colors.
;;; Each of these shall refer to a non nil entry
;;; (due to lanterna-2 vs. lanterna-3, no further detailed inspection of the values happens)
(deftest v0-9-7-constants-available
         (are [c] (not (nil? c))
              (:black const/colors)
              (:white const/colors)
              (:red const/colors)
              (:green const/colors)
              (:blue const/colors)
              (:cyan const/colors)
              (:magenta const/colors)
              (:yellow const/colors)
              (:default const/colors)))

;;; we want to notice if :utf-8 in charsets disappears
(deftest v0-9-7-utf-8-in-charsets
         (are [c] (not (nil? c))
              (:utf-8 const/charsets)))


;;; the bare minimum of graphics styles.
;;; And underline and blinking. What a luxury!
(deftest v0-9-7-styles-available
         (are [c] (not (nil? c))
              (:bold const/styles)
              (:reverse const/styles)
              (:underline const/styles)
              (:blinking const/styles)))

;;; more styles due to more lanterna-3 capabilities
(deftest v0-10-0-styles-available
         (are [c] (not (nil? c))
              (:circled const/styles)
              (:strikethrough const/styles)
              (:fraktur const/styles)))

;;; for the key codes, the stable part in the map is the values,
;;; not the keys. So we're looking at the values of the map here
;;; and assert that all expected ones are there.
;;;
(deftest v0-9-7-keycodes-available
         (let [value-set (set (vals const/key-codes))]
           (are [c] (not= [:missing c] (get value-set c [:missing c]))
                :normal :escape :backspace :left :right :up :down
                :insert :delete :home :end :page-up :page-down :tab
                :reverse-tab :enter :unknown :cursor-location)))

;;; make sure we have the right palettes available
(deftest v0-9-7-palettes-available
         (are [c] (not (nil? c))
              (:gnome const/palettes)
              (:vga const/palettes)
              (:windows-xp const/palettes)
              (:mac-os-x const/palettes)
              (:xterm const/palettes)
              (:putty const/palettes)))

;;; Note: No test for const/enter-styles and const/exit-styles, as lanterna-3 
;;; removed these, is tracking SGR state itself, and has added the 
;;; enableSGR/disableSGR methods on Terminal instead.
;;; Second note: this would usually be a reason to increment the MAJOR version of
;;; clojure-lanterna, but luckily API stability only becomes a semver requirement
;;; for a MAJOR version of >= 1.

;;;; lanterna.input
;;; added in 0.10.0
;;; provides get-key{,stroke}{,-blocking} for what lanterna-3 calls
;;; "input providers"; both terminals and screens qualify for being
;;; an input provider.

(deftest v0-10-0-input-fns
         (are [ivar] (and (var? ivar) (bound? ivar) (fn? (var-get ivar)))
              #'input/get-key 
              #'input/get-keystroke
              #'input/get-key-blocking
              #'input/get-keystroke-blocking))

;;;; lanterna.terminal
(deftest v0-9-7-terminal-fns
         (are [ivar] (and (var? ivar) (bound? ivar) (fn? (var-get ivar)))
              #'terminal/add-resize-listener
              #'terminal/remove-resize-listener
              #'terminal/get-available-fonts
              #'terminal/get-terminal
              #'terminal/start
              #'terminal/stop
              #'terminal/get-size
              #'terminal/move-cursor
              #'terminal/put-character
              #'terminal/put-string
              #'terminal/clear
              #'terminal/set-fg-color
              #'terminal/set-bg-color
              #'terminal/set-style
              #'terminal/remove-style
              #'terminal/reset-styles
              #'terminal/get-key
              #'terminal/get-key-blocking))

;; only testing presence of macros as there's no core/macro?
(deftest v0-9-7-terminal-macros
         (are [ivar] (and (var? ivar) (bound? ivar))
              #'terminal/in-terminal))

;;;; lanterna.screen
(deftest v0-9-7-screen-fns
         (are [ivar] (and (var? ivar) (bound? ivar) (fn? (var-get ivar)))
              #'screen/add-resize-listener
              #'screen/remove-resize-listener
              #'screen/get-screen
              #'screen/start
              #'screen/stop
              #'screen/get-size
              #'screen/redraw
              #'screen/move-cursor
              #'screen/get-cursor
              #'screen/put-string
              #'screen/put-sheet
              #'screen/clear
              #'screen/get-key
              #'screen/get-key-blocking))

;; only testing presence of macros as there's no core/macro?
(deftest v0-9-7-screen-macros
         (are [ivar] (and (var? ivar) (bound? ivar))
              #'screen/in-screen))

