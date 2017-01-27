# Changelog

Here's the list of changes in each released version.

## v1.0.0

* Broke everything; ported to Lanterna 3.0.0-beta3
* Most screen functions which act on the underlying terminal are now in
  `lanterna.terminal`
* Almost all `lanterna.terminal` now also accept screens as arguments
* All input functions are now in `lanterna.input` and work on both screens and
  terminals
* Added `lanterna.graphics` for simple geometric drawing
* All functions with obvious side effects now have bangs

## v0.9.7

* Fixed map destructuring syntax.

## v0.9.6

* Added `screen/get-cursor`.
* Add a new arity to `screen/move-cursor` to let it take a vector.

## v0.9.5

* Relies on Lanterna `2.1.7` to get some bugfixes.
* Fixed the style setting functions.

## v0.9.4

* Relies on Lanterna `2.1.5` to get some bugfixes.

## v0.9.3

* `get-key-blocking` now accepts optional arguments for timeout and interval.
* Added `remove-resize-listener` functions for Screens and Terminals.

## v0.9.2

* Added an experimental `put-sheet` function for screens.  Don't rely on this
  yet -- it's subject to change.
* Relies on a stable release of Lanterna once more.

## v0.9.1

* Added the `clear` functions for terminals and screens.
* Added the `get-size` functions for terminals and screens.
* Relies on a snapshot release of Lanterna so may not be stable.

## v0.9.0

Initial prerelease.  The architecture won't change but the API might.  Until
version `1.0.0` I reserve the right to break things indiscriminately.
