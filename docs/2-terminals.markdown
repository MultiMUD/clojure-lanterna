Terminals
=========

The lowest layer of Lanterna (and thus clojure-lanterna) is a Terminal.

You can use terminals to do the stuff you normally think of using a curses
library for.

[TOC]

Getting a Terminal
------------------

Let's try it out.  Open up a REPL and pull in the namespace:

    :::clojure
    (require '[lanterna.terminal :as t])

Now get a Terminal:

    :::clojure
    (def term (t/get-terminal :swing))

Now we've got a Terminal called `term`.

We're going to force this to be a Swing-based terminal instead of it being in
the console, because we don't want to mess with our REPL.

When you write a standalone program you can use `:text` to force the terminal to
use the console, but we'll talk more about that later.

You may have noticed that nothing seems to have happened.  Now you need to
"start" the terminal to initialize it:

    :::clojure
    (t/start term)

Now you should see a blank terminal that looks like this:

![Blank Terminal](http://i.imgur.com/sQIHO.png)

Writing Text
------------

You can print characters to the current cursor location with `put-character`:

    :::clojure
    (t/put-character term \H)
    (t/put-character term \i)
    (t/put-character term \!)

![Terminal that says Hi](http://i.imgur.com/YyGEz.png)

Notice how the characters didn't overwrite each other?  `put-character` not only
writes the character, it also move the cursor one column to the right.

You might want to make this a bit more convenient:

    :::clojure
    (def put-character-to-term (partial t/put-character term))
    (def write #(dorun (map put-character-to-term %)))

    (write " My name is Steve!")

![Terminal that says Hi my name is Steve](http://i.imgur.com/LECAv.png)

But of course clojure-lanterna already provides that as the function
`put-string`.

Moving the Cursor
-----------------

You can move the cursor with `move-cursor`:

    :::clojure
    (t/move-cursor term 40 12)
    (t/put-character term \@)

This moves the cursor to column 40, row 12 and prints an @.

![Terminal with Rogue](http://i.imgur.com/rN8lK.png)

Let's move the cursor back over the @ so it looks like a proper Roguelike game:

    :::clojure
    (t/move-cursor term 40 12)

![Terminal with Highlighted Rogue](http://i.imgur.com/MADt8.png)

Colors
------

You can change the foreground and background colors:

    :::clojure
    (t/move-cursor term 0 6)
    (t/set-fg-color term :red)
    (t/put-string term "Red")

    (t/move-cursor term 0 7)
    (t/set-fg-color term :blue)
    (t/put-string term "Blue")

    (t/move-cursor term 0 8)
    (t/set-fg-color term :black)
    (t/set-bg-color term :green)
    (t/put-string term "Green")

![Terminal with Colors](http://i.imgur.com/ZkxhC.png)

When you set a foreground or background color, all subsequent characters you
write will use that color.  To reset the colors back to the default you can use
the special color `:default`:

    :::clojure
    (t/set-fg-color term :default)
    (t/set-bg-color term :default)

Styles
------

Styles are not currently implemented for Terminals.  Pull requests are welcome,
I'm pretty sure it's a Clojure/Java interop problem.

Input
-----

Lanterna will buffer the user's keystrokes for you so you can retrieve them
later.

Focus the Swing terminal and type "abc".  Now try running `get-key` in your
REPL:

    :::clojure
    (t/get-key term)
    ; => \a

Lanterna returns the first letter that you typed, as a standard Java/Clojure
Character.

Run it three more times:

    :::clojure
    (t/get-key term)
    ; => \b

    (t/get-key term)
    ; => \c

    (t/get-key term)
    ; => nil

Each call to `get-key` pops one character off the input buffer and returns it.
If there isn't anything on the buffer, it returns `nil`.

If you want to make sure you get a key back (by waiting for the user to press
one if there's none already buffered) you can use `get-key-blocking`:

    :::clojure
    (t/get-key-blocking term)
    ;
    ; Nothing happens until you press a key in the Swing terminal,
    ; then the key is returned.
    ;
    ; => \a

Normal alphanumeric keys are returned as simple Character objects like `\a`.

Note that there's no special attribute to determine if the Shift key was
pressed, but the Characters will be the correct ones.  For example, if the user
presses "Shift-a" the Character you get will be `\A` instead of `\a`.

Special keys are returned as Clojure keywords like `:enter`, `:page-up`, and
`:backspace`.

You can get a full list of the supported special keys by peeking in Lanterna's
constants namespace (or just consult the reference documentation):

    :::clojure
    (require '[lanterna.constants :as c])
    (vals c/key-codes)
    ; => (:end :cursor-location :backspace :unknown :right
    ;     :delete :tab :insert :enter :left :page-up :page-down
    ;     :escape :reverse-tab :home :down :normal :up)

Resizing
--------

The final piece of Lantera's terminal layer is the concept of resizing.

When writing a terminal application, you're at the mercy of the user when it
comes to how big (or small) the window is going to be.

Obviously in a console environment the user can resize their xterm window.
Lanterna's Swing terminal emulator can be resized by dragging normally as well.

Your application needs to be able to handle resized windows.  To do this you can
provide a function when you create the terminal.  This function will be called
by Lanterna whenever the window is resized and passed the new columns and rows.

Let's try it out.  First close your old terminal:

    :::clojure
    (t/stop term)

You'll notice the Swing emulator vanishes.  Let's make a simple listener
function that will update a ref whenever the terminal size changes:

    :::clojure
    (def terminal-size (ref [0 0]))

    (defn handle-resize [cols rows]
      (dosync (ref-set terminal-size [cols rows])))

Create a new Swing terminal, passing an options map containing the listener
function:

    :::clojure
    (def term (t/get-terminal :swing {:resize-listener handle-resize}))

    (t/start term)

If you try to check the size right away, you'll still get `[0 0]`:

    :::clojure
    @terminal-size
    ; => [0 0]

Now resize the Swing window and try again:

    :::clojure
    @terminal-size
    ; => [78 24]

TODO: Figure out how to get the initial size.

What you do in your resize listener is up to you.  You might want to record the
size as we did here, and you might also want to redraw your UI, because it'll
probably look strange otherwise.

That wraps up the terminal layer.  Go ahead and close your terminal:

    :::clojure
    (t/stop term)

One more thing: if you get tired of manually `start`ing and `stop`ing terminals,
you can use the `in-terminal` macro to do it for you:

    :::clojure
    (let [term (t/get-terminal :swing)]
      (t/in-terminal term
        (t/put-string term "Hello!  Press any key to end.")
        (t/get-key-blocking term)))

What's Next?
------------

Now that you've covered all of the major concepts of Lanterna's terminal layer,
it's time to move on to the next layer: [screens](../screens/).
