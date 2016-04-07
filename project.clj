(defproject crisalix "0.1.0-SNAPSHOT"
  :description "A tool for exporting patient 3D scans from Crisalix"
  :url "http://github.com/davidykay/crisalix-exporter"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main crisalix.core
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.5.0"]
                 [clj-http "2.1.0"]
                 [com.taoensso/timbre "4.3.1"]
                 [reaver "0.1.2"]
                 [slingshot "0.12.2"]]
  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :plugins [[lein-midje "3.2"]]}})
