clojure-lanterna is a thin wrapper around the [Lanterna][] Java library to make
it more Clojure-friendly.

[Lanterna][] is a Java library for interacting with terminals.  It's kind of
like curses, except it's pure Java so it'll run anywhere.  It lets you move the
cursor around, draw colored text, and so on.

It also contains a simple Swing "terminal emulator", so you can run your code
either in the terminal or in a separate Swing window when you don't have
a real terminal available (inside an IDE or on a certain terrible OS).

It's particularly nice for writing Roguelikes.

There are three main layers to Lanterna.  This documentation is for
clojure-lanterna, but will cover everything you'll need to know, so you probably
won't need to read Lanterna's docs separately.

Currently clojure-lanterna supports the first two layers of Lanterna: Terminal
and Screen.  At some point support may be added for the GUI layer, but not any
time soon.

Start at the Installation docs, then read the Terminal docs, then the Screen
docs.

[Lanterna]: https://code.google.com/p/lanterna/
