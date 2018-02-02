(ns clj-discord-bot.database
  (:require
    [byte-streams :as b]
    [clojure.java.jdbc :refer :all]
    [clojure.java.io :as io]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(defn init-db []
  (db-do-commands db
                  (str "CREATE TABLE IF NOT EXISTS games ("
                          "server_id INTEGER NOT NULL,"
                          "user_id INTEGER NOT NULL,"
                          "game_name TEXT NOT NULL,"
                          "PRIMARY KEY (server_id, user_id, game_name))")
                  (str "CREATE TABLE IF NOT EXISTS memes ("
                       "meme_text TEXT NOT NULL UNIQUE,"
                       "image BLOB NOT NULL,"
                       "PRIMARY KEY (meme_text))")))

(defn reset-db []
  (do
    (drop-table-ddl "games")
    (drop-table-ddl "memes")
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
      (println (.getMessage e)))))

(defn game-deletion [server-id, user-id, game-name]
      (try
        (delete! db :games ["user-id = ? AND game-name = ?" user-id game-name])
        (catch Exception e
          (println (.getMessage e) e))))

(defn get-meme-image [meme-text]
      (let [img-blob (query db ["SELECT image from memes where meme_text LIKE ?" meme-text])
            fos (java.io.FileOutputStream. "./template.png")]
        (println img-blob)
           (b/transfer (:image (first img-blob)) fos {:append false})
           (.close fos)))

(defn meme-insertion [meme-text]
      (let [fis (java.io.FileInputStream. "./new-meme.png")]
           (try
             (do
               (insert! db :memes {:meme_text meme-text
                                   :image (b/to-byte-array fis)})
               (.close fis))
             (catch Exception e
               (println (.getMessage e) e)))))

(defn meme-deletion [meme-text]
      (try
        (delete! db :memes ["meme_text = ?" meme-text])
        (catch Exception e
          (println (.getMessage e) e))))

(defn get-meme-list []
      (query db ["SELECT meme_text from MEMES"]))
