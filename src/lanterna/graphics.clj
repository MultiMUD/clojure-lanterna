(ns lanterna.graphics
  (:require [lanterna.constants :as c])
  (:import com.googlecode.lanterna.TerminalSize
           com.googlecode.lanterna.TerminalPosition
           com.googlecode.lanterna.SGR
           com.googlecode.lanterna.graphics.TextGraphics
           com.googlecode.lanterna.screen.TerminalScreen))

(defn- styled-graphics
  "Return a TextGraphics object with the corresponding styles"
  [term {:keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
  (let [text (.newTextGraphics term)]
    (doto text
      (.enableModifiers (into-array com.googlecode.lanterna.SGR (map c/styles styles)))
      (.setForegroundColor (c/colors fg))
      (.setBackgroundColor (c/colors bg)))))

(defn draw-line
  "draws a line on the given term from coordinates
  from-x, from-y to to-x, to-y with the given character
  ch. This character is styled with the given 
  foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term [from-x from-y] [to-x to-y] ch]
   (draw-line term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch]
   (draw-line term from-x from-y to-x to-y ch {}))
  ([term from-x from-y to-x to-y ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawLine (styled-graphics term opts)
              (new TerminalPosition from-x from-y)
              (new TerminalPosition to-x to-y)
              ch)
   term))

(defn draw-rectangle
  "draws a rectangle (outline) on the given term at coordinates x, y with
  width w and height h with the given character ch. This character
  is styled with the given foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term [x y] [w h] ch]
   (draw-rectangle term x y w h ch {}))
  ([term x y w h ch]
   (draw-rectangle term x y w h ch {}))
  ([term x y w h ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawRectangle (styled-graphics term opts)
                   (new TerminalPosition x y)
                   (new TerminalSize h w)
                   ch)
   term))

(defn draw-triangle
  "draws a triangle (outline) on the given term between coordinate
  paris ax, ay; bx, by and cx, cy with the given character ch. This character
  is styled with the given foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term [ax ay] [bx by] [cx cy] ch]
   (draw-triangle term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch]
   (draw-triangle term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.drawTriangle (styled-graphics term opts)
                  (new TerminalPosition ax ay)
                  (new TerminalPosition bx by)
                  (new TerminalPosition cx cy)
                  ch)
   term))

(defn fill
  "fills the given term with the given character ch. This character
  is styled with the given foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term ch]
   (fill term ch {}))
  ([term ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.fill (styled-graphics term opts) ch)
   term))

(defn fill-rectangle
  "draws a rectangle (filled) on the given term at coordinates x, y with
  width w and height h with the given character ch. This character
  is styled with the given foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term [x y] [w h] ch]
   (fill-rectangle term x y w h ch {}))
  ([term x y w h ch]
   (fill-rectangle term x y w h ch {}))
  ([term x y w h ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}]
   (.fillRectangle (styled-graphics term opts)
                   (new TerminalPosition x y)
                   (new TerminalSize h w)
                   ch)
   term))

(defn fill-triangle
  "draws a triangle (filled) on the given term between coordinate
  paris ax, ay; bx, by and cx, cy with the given character ch. This character
  is styled with the given foreground (:fg), background (:bg) and
  additional styles (:styles set). returns term."
  ([term [ax ay] [bx by] [cx cy] ch]
   (fill-triangle term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch]
   (fill-triangle term ax ay bx by cx cy ch {}))
  ([term ax ay bx by cx cy ch
    {:as opts :keys [fg bg styles] :or {:fg :default :bg :default :styles #{}}}    ]
   (.fillTriangle (styled-graphics term opts)
                  (new TerminalPosition ax ay)
                  (new TerminalPosition bx by)
                  (new TerminalPosition cx cy)
                  ch)
   term))
