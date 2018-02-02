(defproject clj-discord-bot "0.1.0-SNAPSHOT"
  :dependencies [[byte-streams "0.2.3"]
                 [clj-discord "0.1.0-SNAPSHOT"]
                 [clj-http "3.7.0"]
                 [clj-meme "0.1.0"]
                 [clojail "1.0.6"]
                 [org.clojure/data.json "0.2.6"]
                 [cheshire "5.8.0"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.fversnel/steam-api "0.8.0"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :main clj-discord-bot.core
  :profiles {:uberjar {:aot [clj-discord-bot.core]}}
  :plugins [[lein-cljfmt "0.5.7"]]
  :jvm-opts ["-Djava.security.policy=.java.policy"])
