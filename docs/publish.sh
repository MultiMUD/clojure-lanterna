#!/usr/bin/env bash

rm -rf ./build
~/lib/virtualenvs/d/bin/d
hg -R ~/src/sjl.bitbucket.org pull -u
rsync --delete -a ./build/ ~/src/sjl.bitbucket.org/clojure-lanterna
hg -R ~/src/sjl.bitbucket.org commit -Am 'clojure-lanterna: Update site.'
hg -R ~/src/sjl.bitbucket.org push

