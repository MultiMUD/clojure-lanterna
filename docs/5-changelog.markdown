Changelog
=========

Here's the list of changes in each released version.

Table Of Contents
-----------------

<!-- vim-markdown-toc GFM -->
* [v0.10.0](#v0100)
* [v0.9.7](#v097)
* [v0.9.6](#v096)
* [v0.9.5](#v095)
* [v0.9.4](#v094)
* [v0.9.3](#v093)
* [v0.9.2](#v092)
* [v0.9.1](#v091)
* [v0.9.0](#v090)

<!-- vim-markdown-toc -->

v0.10.0
-------

* New Maintainer, new repo.
* Update lanterna to 3.0-beta3
* Incorporate @AdamNiederer's pull request to the previous repo home:
  * add lanterna.input
  * add (input/get-keystroke-blocking)
  * add (input/get-keystroke) Both of these return a map `{:key char :shift bool :ctrl bool :alt bool}`
  * (input/get-key) and (input/get-key-blocking) are implemented in terms of the above fns
  * use these four input fns in both `lanterna.screen` and `lanterna.terminal`
  * remove `lanterna.common`
  * `(terminal/put-char)` now also accepts `x`/`y` coordinates to write at
  * enter/exit style are gone, lanterna now handles this differently
* add arities to `(terminal/put-char)` to allow (or suppress) immediate flushing after putting the character
* Add an API stability test suite

v0.9.7
------

* Fixed map destructuring syntax.

v0.9.6
------

* Added `screen/get-cursor`.
* Add a new arity to `screen/move-cursor` to let it take a vector.

v0.9.5
------

* Relies on Lanterna `2.1.7` to get some bugfixes.
* Fixed the style setting functions.

v0.9.4
------

* Relies on Lanterna `2.1.5` to get some bugfixes.

v0.9.3
------

* `get-key-blocking` now accepts optional arguments for timeout and interval.
* Added `remove-resize-listener` functions for Screens and Terminals.

v0.9.2
------

* Added an experimental `put-sheet` function for screens.  Don't rely on this
  yet -- it's subject to change.
* Relies on a stable release of Lanterna once more.

v0.9.1
------

* Added the `clear` functions for terminals and screens.
* Added the `get-size` functions for terminals and screens.
* Relies on a snapshot release of Lanterna so may not be stable.

v0.9.0
------

Initial prerelease.  The architecture won't change but the API might.  Until
version `1.0.0` I reserve the right to break things indiscriminately.
