(ns clj-discord-bot.database
  (:require [clojure.java.jdbc :refer :all]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

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

(defn game-query [server-id, game-name]
  (query db ["SELECT * FROM games WHERE server_id=? AND game_name LIKE ?"
             server-id
             (str "%" game-name "%")])) ; wildcards so name can be anywhere

(defn get-users-games [user-id]
  (query db ["SELECT game_name FROM games WHERE user_id=?" user-id]))

(defn game-insertion [server-id, user-id game-name]
  (try
    (insert! db :games {:server_id server-id,
                        :user_id user-id,
                        :game_name game-name})
    (catch Exception e
      (println (.getMessage e) e))))
