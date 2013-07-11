Changelog
=========

Here's the list of changes in each released version.

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
