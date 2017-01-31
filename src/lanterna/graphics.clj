(ns lanterna.graphics
  (:require [lanterna.constants :as c])
  (:import com.googlecode.lanterna.TerminalSize
           com.googlecode.lanterna.TerminalPosition
           com.googlecode.lanterna.SGR
           com.googlecode.lanterna.graphics.TextGraphics
           com.googlecode.lanterna.screen.TerminalScreen))

;; Strangely, screens and terminals are duck-polymorphic in their handling of
;; graphics. We don't need to get the underlying terminals in this situation,
;; unlike in term.clj

(defn- styled-graphics
  "Return a TextGraphics object with the corresponding styles"
  [term {:keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
  (let [text (.newTextGraphics term)]
    (.enableModifiers text (into-array com.googlecode.lanterna.SGR (map c/styles styles)))
    (doto text
      (.setForegroundColor (c/colors fg))
      (.setBackgroundColor (c/colors bg)))
    text))

(defn draw-line!
  ([term [from-x from-y] [to-x to-y] ch]
   (draw-line! term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch]
   (draw-line! term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawLine (styled-graphics term opts)
              (TerminalPosition. from-x from-y)
              (TerminalPosition. to-x to-y)
              ch)))

(defn draw-rectangle!
  ([term [x y] [w h] ch]
   (draw-rectangle! term x y w h ch {}))
  ([term x y w h ch]
   (draw-rectangle! term x y w h ch {}))
  ([term x y w h ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawRectangle (styled-graphics term opts)
                   (TerminalPosition. x y)
                   (TerminalSize. h w)
                   ch)))

(defn draw-triangle!
  ([term [ax ay] [bx by] [cx cy] ch]
   (draw-triangle! term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch]
   (draw-triangle! term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawTriangle (styled-graphics term opts)
                  (TerminalPosition. ax ay)
                  (TerminalPosition. bx by)
                  (TerminalPosition. cx cy)
                  ch)))

(defn fill!
  ([term ch]
   (fill! term ch {}))
  ([term ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.fill (styled-graphics term opts) ch)))

(defn fill-line!
  ([term [from-x from-y] [to-x to-y] ch]
   (fill-line! term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch]
   (fill-line! term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.fillLine (styled-graphics term opts)
              (TerminalPosition. from-x from-y)
              (TerminalPosition. to-x to-y)
              ch)))

(defn fill-rectangle!
  ([term [x y] [w h] ch]
   (fill-rectangle! term x y w h ch {}))
  ([term x y w h ch]
   (fill-rectangle! term x y w h ch {}))
  ([term x y w h ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.fillRectangle (styled-graphics term opts)
                   (TerminalPosition. x y)
                   (TerminalSize. h w)
                   ch)))

(defn fill-triangle!
  ([term [ax ay] [bx by] [cx cy] ch]
   (fill-triangle! term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch]
   (fill-triangle! term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}    ]
   (.fillTriangle (styled-graphics term opts)
                  (TerminalPosition. ax ay)
                  (TerminalPosition. bx by)
                  (TerminalPosition. cx cy)
                  ch)))
