Reference
=========

Here's the deep dive.

If you haven't read the [terminal](../terminals/) and [screen](../screens/)
documentation you should read those to wrap your brain around the structure of
things first.

[TOC]

Constants
---------

clojure-lanterna uses Clojure keywords where you need to supply constants. It
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

and four less-common styles:

* `:circled`
* `:strikethrough`
* `:fraktur`

If your terminal doesn't do anything for those SGR styles, it's probably a bug
in your terminal, rather than in this library.

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
* `:cursor-location` - I'm not sure about this. I think it's an internal
  Lanterna thing.
* `:eof` - Passed when you attempt to get input from a closed terminal

### Charsets

Currently there's only one charset clojure-lanterna constant defines. Open an
issue as a feature request if you want others -- I'll be happy to add the
constants.

* `:utf-8`

### Consoles

When creating a Terminal or Screen, you can optionally specify a specific kind
of Terminal or Screen to create.

If it's not supported (e.g.: trying to create a Swing Terminal on a system
without X) then who knows what will happen. Make sure you know what you're
doing if you use anything other than `:auto`.

* `:auto` - Let Lanterna try to guess the appropriate kind of console to use.
  If there's a windowing environment present the Swing console will be used,
  otherwise an appropriate text console will be used.
* `:text` - Force a text-based (i.e.: non-Swing) console. Lanterna will try to
  guess the appropriate kind of console (UNIX or Cygwin) by the OS.
* `:unix` - Force a UNIX text-based console. This is used for `:text` on Unix
  like operating systems
* `:cygwin` - Force a Cygwin text-based console. This is used for `:text` on
  Windows.

### Palettes

When creating a Swing Terminal or Screen, you can choose the color palette to
use. Text-based Terminals and Screens will use the user's color scheme, of
course.

The following palettes are supported:

* `:gnome` - Gnome Terminal's colors.
* `:windows-xp` - The colors of the Windows XP command prompt.
* `:xterm` - Xterm's colors.
* `:putty` - Putty's colors.
* `:mac-os-x` - The colors of Mac OS X's Terminal.app.

### Font Names

When giving a font name, it should be a string naming a font family on your
system. For example: `"Consolas"`, `"Courier New"`, or `"Monaco"`.

To see a the fonts available on your system you can call
[`get-available-fonts`](#lanternaterminalget-available-fonts).

The font must be monospaced. This is a Lanterna limitation.

Terminals
---------

The terminal layer is the lowest-level layer. Read the [terminal
documentation](../terminals/) for an overview.

### lanterna.terminal/get-terminal

    :::clojure
    (get-terminal)
    (get-terminal kind)
    (get-terminal kind options)

Get a terminal object.

`kind` is a [console constant](#consoles) describing the type of terminal you
want. If unspecified it defaults to `:auto`.

The `options` map can contain any of the following mappings:

* `:cols` - Width of the desired terminal in characters (default `80`).
* `:rows` - Height of the desired terminal in characters (default `24`).
* `:charset` - Charset of the desired terminal. This should be a [charset
  constant](#charsets) (default `:utf-8`).
* `:resize-listener` - A function to call when the terminal is resized. This
  function should take two parameters: the new number of columns, and the new
  number of rows.
* `:font` - A single [font name](#font-names) or sequence of [font
  names](#font-names). If a sequence is given, the first font that exists on
  the system will be used (much like a CSS `font-family` declaration).
  Will fall back to a monospaced default font if none of the given ones exist.

The `:rows`, `:cols`, `:font`, `:font-size`, `:palette` and `:charset` options
are really just a suggestion!

The text-based terminals will ignore rows, columns, fonts and palettes. They
will be determined by the user's terminal window.

The Swing terminal will start out at the given size but can be resized later by
the user, and will ignore the charset entirely.

God only know what Cygwin will do.

Your application needs to be flexible and handle sizes on the fly.

### lanterna.terminal/start!

    :::clojure
    (start! terminal)

Start the given terminal. Terminals must be started before they can be used.

Consider using [`with-terminal`](#lanternaterminalwith-terminal) instead if you
don't need detailed control of the starting and stopping.

### lanterna.terminal/stop!

    :::clojure
    (stop! terminal)

Stop the given terminal. Terminals must be stopped after you're done with them,
otherwise you risk corrupting the user's console.

Don't try to do anything to the Terminal after you stop it.

Consider using [`with-terminal`](#lanternaterminalwith-terminal) instead if you
don't need detailed control of the starting and stopping.

### lanterna.terminal/with-terminal

    :::clojure
    (with-terminal terminal & body)

Start the given terminal, perform the body of expressions, and stop the terminal
afterward.

This is a macro.

The stopping will be done in a try/finally block, so you can be confident it
will actually happen.

Use this if you don't need detailed control of the terminal starting and
stopping process.

### lanterna.terminal/get-size

    :::clojure
    (get-size terminal)

Return the current size of the terminal as `[cols rows]`.

### lanterna.terminal/move-cursor!

    :::clojure
    (move-cursor! terminal x y)

Move the cursor to a specific location on the screen.

### lanterna.terminal/put-char!

    :::clojure
    (put-char! terminal ch)

Draw the character at the current cursor location.

Also moves the cursor one character to the right, so a sequence of calls will
output next to each other.

    :::clojure
    (put-char! terminal ch x y)

Draw the character at the specified cursor location.

Also moves the cursor one character to the right.

### lanterna.terminal/put-string!

    :::clojure
    (put-string! terminal s)

Draw the string at the current cursor location.

The cursor will end up at the position directly after the string.

    :::clojure
    (put-string! terminal s x y)

Draw the string at the specified cursor location.

The cursor will end up at the position directly after the string.

### lanterna.terminal/clear!

    :::clojure
    (clear! terminal)

Clear the given terminal.

The cursor will be at the coordinates 0, 0 after the clearing.

### lanterna.terminal/set-fg!

    :::clojure
    (set-fg! terminal color)

Set the foreground color for text drawn by subsequent
[`put-char`](#lanternaterminalput-char) and
[`put-string`](#lanternaterminalput-string) calls.

Color is a [color constant](#colors) like `:red`.

### lanterna.terminal/set-bg!

    :::clojure
    (set-bg! terminal color)

Set the background color for text drawn by subsequent
[`put-char!`](#lanternaterminalput-char!) and
[`put-string`](#lanternaterminalput-string) calls.

Color is a [color constant](#colors) like `:red`.

### lanterna.terminal/set-style

    :::clojure
    (set-style! terminal style)

### lanterna.terminal/reset-styles!

    :::clojure
    (reset-styles! terminal)

### lanterna.input/get-key

    :::clojure
    (get-key terminal)

Get the next keypress from the user, or `nil` if none are buffered.

If there is one or more keystroke buffered, that key will be returned (and
popped off the buffer of input). The returned key will be a [key code
constant](#key-codes).

If there are no keystrokes buffered, `nil` will be returned immediately.

If you want to wait for user input, use
[`get-key-blocking`](#lanternaterminalget-key-blocking) instead.

### lanterna.terminal/get-key-blocking

    :::clojure
    (get-key-blocking terminal)
    (get-key-blocking terminal options)

Get the next keypress from the user.

If there is one or more keystroke buffered, that key will be returned (and
popped off the buffer of input). The returned key will be a [key code
constant](#key-codes).

If there are no keystrokes buffered the function will sleep, checking every 50
milliseconds for input. Once there is a character buffered it will be popped
off and returned as normal.

If you want to return immediately instead of blocking when no input is buffered,
use [`get-key`](#lanternaterminalget-key) instead.

The `options` map can contain any of the following mappings:

* `:interval` - The interval between checks, in milliseconds (default `50`).
* `:timeout` - The maximum amount of time blocking will occur before returning
  `nil` (default infinity).

### lanterna.terminal/add-resize-listener

    :::clojure
    (add-resize-listener terminal listener-fn)

Create a listener that will call the supplied function when the terminal is
resized.

The function must take two arguments: the new number of columns and the new
number of rows.

You probably don't need this because you can specify a resize listener function
when you call [`get-terminal`](#lanternaterminalget-terminal). It's here if you
*do* need it though.

### lanterna.terminal/remove-resize-listener

    :::clojure
    (remove-resize-listener terminal listener)

Remove the given resize listener from the given terminal.

### lanterna.terminal/get-available-fonts

    :::clojure
    (get-available-fonts)

Return a set of strings of the names of available fonts on the current system.

Screens
-------

The screen layer is an abstraction that provides buffering on top of the
terminal layer. Read the [screen documentation](../screens/) for an overview.

### lanterna.screen/get-screen

    :::clojure
    (get-screen)
    (get-screen kind)
    (get-screen kind options)

Get a screen object.

`kind` is a [console constant](#consoles) describing the type of screen you
want. If unspecified it defaults to `:auto`.

The `options` map can contain any of the following mappings:

* `:cols` - Width of the desired screen in characters (default `80`).
* `:rows` - Height of the desired screen in characters (default `24`).
* `:charset` - Charset of the desired screen. This should be a [charset
  constant](#charsets) (default `:utf-8`).
* `:resize-listener` - A function to call when the screen is resized. This
  function should take two parameters: the new number of columns, and the new
  number of rows.
* `:font` - A single [font name](#font-names) or sequence of [font
  names](#font-names). If a sequence is given, the first font that exists on
  the system will be used (much like a CSS `font-family` declaration).
  Will fall back to a monospaced default font if none of the given ones exist.

The `:rows`, `:cols`, and `:charset` options are really just a suggestion!

The text-based screens will ignore rows and columns and will be the size of
the user's window.

The Swing screen will start out at this size but can be resized later by the
user, and will ignore the charset entirely.

God only know what Cygwin will do.

Your application needs to be flexible and handle sizes on the fly.

### lanterna.screen/start

    :::clojure
    (start screen)

Start the given screen. Screens must be started before they can be used.

Consider using [`with-screen`](#lanternascreenwith-screen) instead if you don't need
detailed control of the starting and stopping.

### lanterna.screen/stop

    :::clojure
    (stop screen)

Stop the given screen. Screens must be stopped after you're done with them,
otherwise you risk corrupting the user's console.

Don't try to do anything to the screen after you stop it.

I'm not sure if you can "restart" a screen once it's been stopped. TODO: Find
out.

Consider using [`with-screen`](#lanternascreenwith-screen) instead if you don't need
detailed control of the starting and stopping.

### lanterna.screen/with-screen

    :::clojure
    (with-screen screen & body)

Start the given screen, perform the body of expressions, and stop the screen
afterward.

This is a macro.

The stopping will be done in a try/finally block, so you can be confident it
will actually happen.

Use this if you don't need detailed control of the screen starting and stopping
process.

### lanterna.screen/get-size

    :::clojure
    (get-size screen)

Return the current size of the screen as `[cols rows]`.

### lanterna.screen/redraw!

    :::clojure
    (redraw! screen)

Redraw the given screen.

This is how you actually flush any changes to the user's display.

### lanterna.screen/get-cursor

    :::clojure
    (get-cursor screen x y)

Retrieve the current location of the cursor on the screen as `[x y]`.

### lanterna.screen/move-cursor!

    :::clojure
    (move-cursor! screen x y)
    (move-cursor! screen [x y])

Move the cursor to a specific location on the screen.

You'll need to [`redraw`](#lanternascreenredraw!) the screen to actually see it
happen.

The cursor will stay where you move it, even if you later draw some text in
a different place and redraw. If you want it to move, you need to call this
function again.

### lanterna.screen/put-string!

    :::clojure
    (put-string! screen x y s)
    (put-string! screen x y s options)

Put a string on the screen buffer, ready to be drawn at the next
[`redraw`](#lanternascreenredraw!).

`x` and `y` are the column and row to start the string.

`s` is the actual string to draw.

The `options` map can contain any of the following mappings:

* `:fg` - Foreground color of the text. Must be a [color constant](#colors)
  (default `:default`).
* `:bg` - Background color of the text. Must be a [color constant](#colors)
  (default `:default`).
* `:styles` - Styles to apply to the text. Must be a set containing zero or
  more [style constants](#styles) (default `#{}`).

### lanterna.screen/clear!

    :::clojure
    (clear! screen)

Clear the given screen.

Note that this is buffered just like every other screen-related action. You
need to [`redraw`](#lanternascreenredraw!) to actually see it happen.

### lanterna.screen/add-resize-listener!

    :::clojure
    (add-resize-listener! screen listener-fn)

Create a listener that will call the supplied function when the screen is
resized.

The function must take two arguments: the new number of columns and the new
number of rows.

You probably don't need this because you can specify a resize listener function
when you call [`get-screen`](#lanternascreenget-screen). It's here if you *do*
need it though.

### lanterna.screen/remove-resize-listener!

    :::clojure
    (remove-resize-listener! screen listener)

Remove the given resize listener from the given screen.

## TODO: Document input functions and graphics module
