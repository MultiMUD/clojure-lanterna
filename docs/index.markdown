clojure-lanterna is a thin wrapper around the [Lanterna][] Java library to make
it more Clojure-friendly.

Table Of Contents
-----------------

<!-- vim-markdown-toc GFM -->
* [What is It?](#what-is-it)
* [Notes](#notes)
* [How to Use It](#how-to-use-it)
* [I Want a Hello, World!](#i-want-a-hello-world)
* [Further Documentation](#further-documentation)

<!-- vim-markdown-toc -->

What is It?
-----------

[Lanterna][] is a Java library for interacting with terminals.  It's kind of
like curses, except it's pure Java so it'll run anywhere.  It lets you move the
cursor around, draw colored text, and so on.

It also contains a simple Swing "terminal emulator", so you can run your code
either in the terminal or in a separate Swing window when you don't have
a real terminal available (inside an IDE or on a certain terrible OS).

It's particularly nice for writing Roguelikes.

Notes
-----

**It's still pre-1.0, so expect brokenness and backwards incompatibility.  Once
it hits 1.0 you can be confident I'll stop breaking your code all the time.**

**License:** GNU Lesser GPL (yes, you can link with code under another license!)  
**Documentation:** <https://multimud.github.io/clojure-lanterna/>  
**Issues:** <https://github.com/multimud/clojure-lanterna/issues/>  
**Git:** <https://github.com/multimud/clojure-lanterna/>  


How to Use It
-------------

There are three main layers to Lanterna.  Currently clojure-lanterna supports
the first two layers of Lanterna: Terminal and Screen.  At some point support
may be added for the GUI layer, but not any time soon.

To get started:

1. Read the [first page][lanterna-docs] of the [Lanterna
   documentation][lanterna-docs].  It sums up some main concepts and things
   you'll need to watch out for.

2. Come back here and read the [Installation](./1-installation) docs to get
   clojure-lanterna on your machine.

3. Read the [Terminal](./2-terminals) and [Screen](./3-screens) documents here in
   order.

4. Have fun!  Consult the [Reference](./4-reference) document if you need more
   detailed information about something.  Most functions also have docstrings.

5. Looking for changes across clojure-lanterna versions? Try the [Changelog](./5-changelog)

[Lanterna]: https://github.com/mabe02/lanterna
[lanterna-docs]: https://code.google.com/archive/p/lanterna/wikis/DevelopmentGuide.wiki

I Want a Hello, World!
----------------------

Okay, fine:

    :::clojure
    (require '[lanterna.screen :as s])

    (def scr (s/get-screen))

    (s/start scr)

    (s/put-string scr 10 10 "Hello, world!")
    (s/put-string scr 10 11 "Press any key to exit!")
    (s/redraw scr)
    (s/get-key-blocking scr)

    (s/stop scr)

But really, please read the docs if you actually want to use this.  They're not
that long.

Further Documentation
---------------------

* [Installation](./1-installation)
* [Terminals](./2-terminals)
* [Screens](./3-screens)
* [Reference](./4-reference)
* [Changelog](./5-changelog)
