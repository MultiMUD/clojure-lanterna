(defproject clojure-lanterna "0.1.0-SNAPSHOT"
  :description "A Clojure wrapper around the Lanterna terminal output library."
  :url "http://sjl.bitbucket.org/clojure-lanterna/"
  :license {:name "LGPL"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.googlecode.lanterna/lanterna "2.0.0"]]
  :repositories {"sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}
  :java-source-paths ["src/java"]
  ; :main lanterna.screen
  )
