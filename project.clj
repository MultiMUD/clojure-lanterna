(defproject org.clojars.folcon/clojure-lanterna "0.9.5-SNAPSHOT"
  :description "A Clojure wrapper around the Lanterna terminal output library."
  :url "http://sjl.bitbucket.org/clojure-lanterna/"
  :license {:name "LGPL"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 ;[com.googlecode.lanterna/lanterna "2.1.7"]
                 [org.clojars.folcon/lanterna "3.0.0-SNAPSHOT"]]
  :java-source-paths ["./java"]
  ; :repositories {"sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}
  )
