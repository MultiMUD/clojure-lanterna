Screens
=======

The next layer of Lanterna is the screen layer.  Think of screens as "[double
buffering][] for your console".

[double buffering]: https://en.wikipedia.org/wiki/Multiple_buffering#Double_buffering_in_computer_graphics

Screens act as a buffer.  You "draw" to the screen like you would normally draw
directly to the terminal, but it doesn't appear to the user.

When you're ready you tell the Screen to redraw.  It will calculate the
necessary changes and make them happen.

This makes it easy to avoid showing half-drawn UIs to your users.

[TOC]

Getting a Screen
----------------

Let's get started.  Open up a REPL and pull in the namespace:

    :::clojure
    (require '[lanterna.screen :as s])

Much like getting a Terminal, you get a Screen with `get-screen`:

    :::clojure
    (def scr (s/get-screen))

`get-screen` supports all the same types of console as `get-terminal`.

You need to `start` and `stop` a Screen before/after use just like a Terminal:

    :::clojure
    (s/start! scr)
    ; ... do things ...
    (s/stop! scr)

There's an `in-screen` helper too:

    :::clojure
    (let [scr (s/get-screen :swing)]
      (with-screen scr
        ; do things with scr
        ))


Writing Text
------------

You can pass any Screen object into any Terminal function; clojure-lanterna
automatically gets the underlying terminal and operates on it.

    :::clojure
    (t/put-string! scr "Hello, world!")

When you run this, nothing will happen.  This is because Screens buffer their
output.  You need to redraw the screen to see any changes:

    :::clojure
    (s/redraw! scr)

![Screen](http://i.imgur.com/79Qr1.png)

You can of course queue up many updates before redrawing -- that's the whole
point of a screen!

    :::clojure
    (t/put-string! scr "Hello, world!" 20 10)
    (t/put-string! scr "Steve" 27 10)
    (s/redraw! scr)

![Screen with More](http://i.imgur.com/tLm16.png)

This will display "Hello, Steve!", which demonstrates that you can overwrite
a single character as much as you want before a redraw and the correct result
will be shown.

Note that because we haven't touched the upper-left corner in this redraw our
original "Hello, world!" is still there.  Screens redraw *changes*, they don't
start from scratch every time.

You can clear a screen with `(s/clear! scr)`.

### Colors

Colors work exactly as they do in terminals. Either provide it to `put-string!`,
or use `with-styles`, set some colors, and do a bunch of operations with those
colors.

    :::clojure
    (t/put-string! scr "Red" 0 12 {:fg :red})
    (t/put-string! scr "Green" 0 13 {:fg :green})
    (t/put-string! scr "Yellow" 0 14 {:fg :black :bg :yellow})
    (s/redraw! scr)

![Screen with Colors](http://i.imgur.com/uC1qk.png)

### Styles

Drawing styles is also done via the put-string! function. Because styles are
modal, stateful things at the underlying terminal layer, you can also use
(t/set-style! term :kw), perform a bunch of actions, and call
(t/reset-styles! term scr) when you're done. The `(t/with-styles)` macro will
automatically reset styles for you, if you choose to do this.

Note that `reset-styles!` will reset your oclors as well. This is because of
Lanterna's underlying API.

Moving the Cursor
-----------------

Just like the terminal layer, you might want to move the cursor when using
a Screen.

There's a `move-cursor!` function that works like the terminal one to do this:

    :::clojure
    (t/put-string! scr 5 5 "@")
    (t/move-cursor! scr 5 5)
    (s/redraw! scr)

![Screen with Cursor Moved](http://i.imgur.com/gQ2FO.png)

Notice that you have to redraw the screen before the cursor will actually move.

The cursor will stay where you put it, even after other updating and redraws:

    :::clojure
    (t/put-string! scr 5 5 " ")
    (t/put-string! scr 6 5 "@")
    (s/redraw! scr)

![Screen with Cursor Unmoved](http://i.imgur.com/XTd1I.png)

See how the cursor is still in the original spot (5, 5)?  If you want it to move
you need to tell it to move with `move-cursor`.

Input
-----

Getting input works exactly like the terminal layer:

    :::clojure
    (i/get-key scr)
    ; => nil

    (i/get-key-blocking scr)
    ; => :page-down

    (i/get-key scr)
    ; => \S

Sizing
------

Sizing works the same way as the terminal layer.

    :::clojure
    (t/get-size scr)
    ; => [130 44]

You can pass a resize listening function when you create your screen:

    :::clojure
    (def screen-size (ref [0 0]))

    (defn handle-resize [cols rows]
      (dosync (ref-set screen-size [cols rows])))

    (def scr (s/get-screen :swing {:resize-listener handle-resize}))

What's Next?
------------

Now that you can use the screen layer for double-buffered console rendering
you've got pretty much everything you need.  Go make something!

The [Reference documentation](../reference/) has all the detailed information
you'll probably find yourself looking for once you actually dive in and start
building.
