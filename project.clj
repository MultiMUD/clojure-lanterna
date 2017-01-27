(defproject clojure-lanterna "1.0.0"
  :description "A Clojure wrapper around the Lanterna terminal output library."
  :url "http://sjl.bitbucket.org/clojure-lanterna/"
  :license {:name "LGPL"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.googlecode.lanterna/lanterna "3.0.0-beta3"]]
  :java-source-paths ["./java"]
  ; :repositories {"sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}
  :repositories {"releases" {:url "https://clojars.org/repo"
                             :username :env
                             :password :env
                             :sign-releases false}})
