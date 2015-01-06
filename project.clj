(defproject hyper-clj "0.1.0-SNAPSHOT"
  :description "Clojure library for representing hypermedia"
  :url "https://github.com/aprobus/hyper-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :plugins [[speclj "2.5.0"]]
  :source-paths ["src"]
  :profiles {:dev 
             {:dependencies [[speclj "2.5.0"]]}})
