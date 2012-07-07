Reference
=========

Here's the deep dive.

If you haven't read the [terminal](../terminals/) and [screen](../screens/)
documentation you should read those to wrap your brain around the structure of
things first.

[TOC]

Constants
---------

clojure-lanterna uses Clojure keywords where you need to supply constants.  It
will handle the filthy details of converting them to the appropriate Java enum
elements when needed so you don't need to worry about it.

### Colors

Lanterna (and thus clojure-lanterna) supports the 8 common terminal colors, as
well as a "default" color:

* `:black`
* `:white`
* `:red`
* `:green`
* `:blue`
* `:cyan`
* `:magenta`
* `:yellow`
* `:default`

### Styles

Lanterna (and thus clojure-lanterna) supports 4 common styles:

* `:bold`
* `:reverse`
* `:underline`
* `:blinking`

### Key Codes

When you get a key of user input from clojure-lanterna it will be one of two
things: a Character like `\a` or `\$` representing what the user typed, or
a keyword for special keys like Delete or Page Up.

Note that the Tab key is returned as `:tab` and not `\tab`.

Here are the keywords for special keys that may be returned:

* `:escape`
* `:backspace`
* `:left`
* `:right`
* `:up`
* `:down`
* `:insert`
* `:delete`
* `:home`
* `:end`
* `:page-up`
* `:page-down`
* `:tab`
* `:reverse-tab`
* `:enter`

There are also two other special keywords:

* `:unknown` - The user typed something Lanterna couldn't figure out.
* `:cursor-location` - I'm not sure about this.  I think it's an internal
  Lanterna thing.

### Charsets

Currently there's only one charset clojure-lanterna constant defines.  Open an
issue as a feature request if you want others -- I'll be happy to add the
constants.

* `:utf-8`

Terminals
---------

### lanterna.terminal/get-terminal
### lanterna.terminal/start
### lanterna.terminal/stop
### lanterna.terminal/in-terminal
### lanterna.terminal/move-cursor
### lanterna.terminal/put-character
### lanterna.terminal/put-string
### lanterna.terminal/set-fg-color
### lanterna.terminal/set-bg-color
### lanterna.terminal/set-style
### lanterna.terminal/remove-style
### lanterna.terminal/reset-styles
### lanterna.terminal/get-key
### lanterna.terminal/get-key-blocking
### lanterna.terminal/add-resize-listener

Screens
-------

### lanterna.screen/get-screen
### lanterna.screen/start
### lanterna.screen/stop
### lanterna.screen/in-screen
### lanterna.screen/redraw
### lanterna.screen/move-cursor
### lanterna.screen/put-string
### lanterna.screen/get-key
### lanterna.screen/get-key-blocking
### lanterna.screen/add-resize-listener
