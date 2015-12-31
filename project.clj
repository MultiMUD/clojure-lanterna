(defproject clojure-lanterna "3.0.0-beta1-SNAPSHOT"
  :description "A Clojure wrapper around the Lanterna terminal output library."
  :url "http://sjl.bitbucket.org/clojure-lanterna/"
  :license {:name "LGPL"
            :url "https://www.gnu.org/licenses/lgpl.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.googlecode.lanterna/lanterna "3.0.0-beta1"]]
  :aot :all)
  ;:java-source-paths ["./java"])
