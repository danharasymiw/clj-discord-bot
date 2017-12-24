(ns clj-discord-bot.database
  (:require [clojure.java.jdbc :refer :all]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"
   })

(defn init-db []
  (db-do-commands db (str "CREATE TABLE IF NOT EXISTS games ("
                          "server_id INTEGER NOT NULL,"
                          "user_id INTEGER NOT NULL,"
                          "game_name TEXT NOT NULL,"
                          "PRIMARY KEY (server_id, user_id, game_name))")))

(defn reset-db []
  (do
    (drop-table-ddl "games")
    (init-db)))

(defn game-query [server_id, game_name]
  (query db ["SELECT * FROM games WHERE server_id=? AND game_name LIKE ?"
             server_id
             (str "%" game_name "%")])) ; wildcards so name can be anywhere

(defn game-insertion [server_id, user_id game_name]
  (try
    (insert! db :games {:server_id server_id,
                        :user_id user_id,
                        :game_name game_name})
    (catch Exception e))) ; i dont care
