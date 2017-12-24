(ns clj-discord-bot.database
  (:require [clojure.java.jdbc :refer :all]))

(def testdata
  {:server_id 1,
   :game_name "Jak II",
   :user_id 123
   })

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
   })

(defn init-db []
  (db-do-commands db (str "CREATE TABLE IF NOT EXISTS games ("
                          "server_id INTEGER NOT NULL,"
                          "game_name TEXT NOT NULL,"
                          "user_id INTEGER NOT NULL,"
                          "PRIMARY KEY (server_id, game_name))")))

(init-db)
(insert! db :games testdata)

(def output
  (query db "select * from games"))
