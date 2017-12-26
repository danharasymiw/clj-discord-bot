(defproject clj-discord-bot "0.1.0-SNAPSHOT"
  :dependencies [[clj-discord "0.1.0-SNAPSHOT"]
                 [clj-http "3.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [cheshire "5.8.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :main clj-discord-bot.core
  :profiles {:uberjar {:aot [clj-discord-bot.core]}}
  :plugins [[lein-cljfmt "0.5.7"]])
