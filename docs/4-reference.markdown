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

### Consoles

When creating a Terminal or Screen, you can optionally specify a specific kind
of Terminal or Screen to create.

If it's not supported (e.g.: trying to create a Swing Terminal on a system
without X) then who knows what will happen.  Make sure you know what you're
doing if you use anything other than `:auto`.

* `:auto` - Let Lanterna try to guess the appropriate kind of console to use.
  If there's a windowing environment present the Swing console will be used,
  otherwise an appropriate text console will be used.
* `:swing` - Force a Swing-based console.
* `:text` - Force a text-based (i.e.: non-Swing) console.  Lanterna will try to guess the
  appropriate kind of console (UNIX or Cygwin) by the OS.
* `:unix` - Force a UNIX text-based console.
* `:cygwin` - Force a Cygwin text-based console.

Terminals
---------

### lanterna.terminal/get-terminal

    :::clojure
    (get-terminal)
    (get-terminal kind)
    (get-terminal kind options)

Get a terminal object.

`kind` is a [console constant](#consoles) describing the type of terminal you
want.  If unspecified it defaults to `:auto`.

Options can contain one or more of the following mappings:

* `:cols` - Width of the desired terminal in characters (default `80`).
* `:rows` - Height of the desired terminal in characters (default `24`).
* `:charset` - Charset of the desired terminal.  This should be a [charset
  constant](#charsets) (default `:utf-8`).
* `:resize-listener` - A function to call when the terminal is resized.  This
  function should take two parameters: the new number of columns, and the new
  number of rows.

The `:rows`, `:cols`, and `:charset` options are really just a suggestion!

The text-based terminals will ignore rows and columns and will be the size of
the user's window.

The Swing terminal will start out at this size but can be resized later by the
user, and will ignore the charset entirely.

God only know what Cygwin will do.

Your application needs to be flexible and handle sizes on the fly.

### lanterna.terminal/start

    :::clojure
    (start terminal)

Start the given terminal.  Terminals must be started before they can be used.

Consider using [`in-terminal`](#lanternaterminalin-terminal) instead if you
don't need detailed control of the starting and stopping.

### lanterna.terminal/stop

    :::clojure
    (stop terminal)

Stop the given terminal.  Terminals must be stopped after you're done with them,
otherwise you risk corrupting the user's console.

Don't try to do anything to the Terminal after you stop it.

I'm not sure if you can "restart" a terminal once it's been stopped.  TODO: Find
out.

Consider using [`in-terminal`](#lanternaterminalin-terminal) instead if you
don't need detailed control of the starting and stopping.

### lanterna.terminal/in-terminal

    :::clojure
    (in-terminal terminal & body)

Start the given terminal, perform the body of expressions, and stop the terminal
afterward.

This is a macro.

The stopping will be done in a try/finally block, so you can be confident it
will actually happen.

Use this if you don't need detailed control of the terminal starting and
stopping process.

### lanterna.terminal/move-cursor

    :::clojure
    (move-cursor terminal x y)

Move the cursor to a specific location on the screen.

### lanterna.terminal/put-character

    :::clojure
    (put-character terminal ch)

Draw the character at the current cursor location.

Also moves the cursor one character to the right, so a sequence of calls will
output next to each other.

    :::clojure
    (put-character terminal ch x y)

Draw the character at the specified cursor location.

Also moves the cursor one character to the right.

### lanterna.terminal/put-string

    :::clojure
    (put-string terminal s)

Draw the string at the current cursor location.

The cursor will end up at the position directly after the string.

    :::clojure
    (put-string terminal s x y)

Draw the string at the specified cursor location.

The cursor will end up at the position directly after the string.

### lanterna.terminal/set-fg-color

    :::clojure
    (set-fg-color terminal color)

Set the foreground color for text drawn by subsequent
[`put-character`](#lanternaterminalput-character) and
[`put-string`](#lanternaterminalput-string) calls.

Color is a [color constant](#colors) like `:red`.

### lanterna.terminal/set-bg-color

    :::clojure
    (set-bg-color terminal color)

Set the background color for text drawn by subsequent
[`put-character`](#lanternaterminalput-character) and
[`put-string`](#lanternaterminalput-string) calls.

Color is a [color constant](#colors) like `:red`.

### lanterna.terminal/set-style

Broken right now, sorry.

### lanterna.terminal/remove-style

Broken right now, sorry.

### lanterna.terminal/reset-styles

Broken right now, sorry.

### lanterna.terminal/get-key

    :::clojure
    (get-key terminal)

Get the next keypress from the user, or `nil` if none are buffered.

If there is one or more keystroke buffered, that key will be returned (and
popped off the buffer of input).  The returned key will be a [key code
constant](#key-codes).

If there are no keystrokes buffered, `nil` will be returned immediately.

If you want to wait for user input, use
[`get-key-blocking`](#lanternaterminalget-key-blocking) instead.

### lanterna.terminal/get-key-blocking

    :::clojure
    (get-key-blocking terminal)

Get the next keypress from the user.

If there is one or more keystroke buffered, that key will be returned (and
popped off the buffer of input).  The returned key will be a [key code
constant](#key-codes).

If there are no keystrokes buffered the function will sleep, checking every 50
milliseconds for input.  Once there is a character buffered it will be popped
off and returned as normal.

If you want to return immediately instead of blocking when no input is buffered,
use [`get-key`](#lanternaterminalget-key) instead.

### lanterna.terminal/add-resize-listener

    :::clojure
    (add-resize-listener terminal listener-fn)

Create a listener that will call the supplied function when the terminal is
resized.

The function must take two arguments: the new number of columns and the new
number of rows.

You probably don't need this because you can specify a resize listener function
when you call [`get-terminal`](#lanternaterminalget-terminal).  It's here if you
*do* need it though.

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
