(defproject donotcall "0.0.3"
  :description "A simple REST app for checking if a number is in the telemarketing list or not."
  :url "http://github.com/sam/clojure-donotcall-checker"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.3"]
                 [org.clojars.gjahad/debug-repl "0.3.3"]
                 [hiccup "1.0.5"]
                 [org.clojure/data.json "0.2.6"]]
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler donotcall.web/handler :uberwar-name "donotcall-standalone.war" :port 3001}
  :main ^:skip-aot donotcall.web
  :uberjar-name "donotcall-standalone.jar"
  :profiles {:uberjar {:aot :all}})
