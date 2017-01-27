clojure-lanterna is a functional wrapper around the [Lanterna][] TUI library to
make it more Clojurey.

**It's still pre-1.0, so expect brokenness and backwards incompatibility. Once
it hits 1.0 you can be confident I'll stop breaking your shit all the time.**

**License:** GNU Lesser GPL (yes, you can link with code under another license!)
**Issues:** <http://github.com/AdamNiederer/clojure-lanterna/issues/>
**Git:** <http://github.com/AdamNiederer/clojure-lanterna/>

What is It?
-----------

[Lanterna][] is a Java library for interacting with terminals. It's kind of
like curses, except it's pure Java so it'll run anywhere. It lets you move the
cursor around, draw colored text, and so on.

It also contains a simple Swing "terminal emulator", so you can run your code
either in the terminal or in a separate Swing window when you don't have
a real terminal available (inside an IDE or on a certain terrible OS).

It's particularly nice for writing Roguelikes.

How to Use It
-------------

There are three main layers to Lanterna. Currently clojure-lanterna supports
the first two layers of Lanterna: Terminal and Screen. At some point support
may be added for the GUI layer, but not any time soon.

To get started:

1. Read the [first page][lanterna-docs] of the [Lanterna
   documentation][lanterna-docs]. It sums up some main concepts and things
   you'll need to watch out for.

2. Come back here and read the [Installation](./installation/) docs to get
   clojure-lanterna on your machine.

3. Read the [Terminal](./terminals/) and [Screen](./screens/) documents here in
   order.

4. Have fun!  Consult the [Reference](./reference/) document if you need more
   detailed information about something. Most functions also have docstrings.

[Lanterna]: https://code.google.com/p/lanterna/
[lanterna-docs]: https://code.google.com/p/lanterna/wiki/DevelopmentGuide

I Want a Hello, World!
----------------------

Okay, fine:

    :::clojure
    (require '[lanterna.screen :as s])
    (require '[lanterna.terminal :as t])

    (def scr (s/get-screen))

    (s/start! scr)

    (s/redraw! scr)
    (t/put-string! scr 10 10 "Hello, world!")
    (t/put-string! scr 10 11 "Press any key to exit!")
    (s/redraw! scr)
    (i/get-keystroke-blocking scr)

    (s/stop! scr)
