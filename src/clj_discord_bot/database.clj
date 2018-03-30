(ns clj-discord-bot.database
  (:require
   [byte-streams :as b]
   [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(defn init-db []
  (jdbc/db-do-commands db
                  (str "CREATE TABLE IF NOT EXISTS games ("
                       "server_id INTEGER NOT NULL,"
                       "user_id INTEGER NOT NULL,"
                       "game_name TEXT NOT NULL,"
                       "PRIMARY KEY (server_id, user_id, game_name))")
                  (str "CREATE TABLE IF NOT EXISTS memes ("
                       "meme_text TEXT NOT NULL UNIQUE,"
                       "image BLOB NOT NULL,"
                       "PRIMARY KEY (meme_text))")
                  (str "CREATE TABLE IF NOT EXISTS wow ("
                       "name TEXT NOT NULL,"
                       "realm TEXT NOT NULL,"
                       "bracket TEXT NOT NULL,"
                       "rating INTEGER NOT NULL,"
                       "PRIMARY KEY (name, realm, bracket))")
                  ))

(defn reset-db []
  (do
    (jdbc/drop-table-ddl "games")
    (jdbc/drop-table-ddl "memes")
    (jdbc/drop-table-ddl "wow")
    (init-db)))

(defn game-query [server-id, game-name]
  (jdbc/query db ["SELECT * FROM games WHERE server_id=? AND game_name LIKE ?"
             server-id
             (str "%" game-name "%")])) ; wildcards so name can be anywhere

(defn get-users-games [user-id]
  (jdbc/query db ["SELECT game_name FROM games WHERE user_id=?" user-id]))

(defn game-insertion [server-id, user-id game-name]
  (try
    (jdbc/insert! db :games {:server_id server-id,
                        :user_id user-id,
                        :game_name game-name})
    (catch Exception e
      (println (.getMessage e)))))

(defn game-deletion [server-id, user-id, game-name]
  (try
    (jdbc/delete! db :games ["user_id = ? AND game_name = ?" user-id game-name])
    (catch Exception e
      (println (.getMessage e) e))))

(defn get-meme-image [meme-text]
  (let [img-blob (jdbc/query db ["SELECT image from memes where meme_text LIKE ?" meme-text])
        fos (java.io.FileOutputStream. "./template.png")]
    (println img-blob)
    (b/transfer (:image (first img-blob)) fos {:append false})
    (.close fos)))

(defn meme-insertion [meme-text]
  (let [fis (java.io.FileInputStream. "./new-meme.png")]
    (try
      (do
        (jdbc/insert! db :memes {:meme_text meme-text
                            :image (b/to-byte-array fis)})
        (.close fis))
      (catch Exception e
        (println (.getMessage e) e)))))

(defn meme-deletion [meme-text]
  (try
    (jdbc/delete! db :memes ["meme_text = ?" meme-text])
    (catch Exception e
      (println (.getMessage e) e))))

(defn get-meme-list []
  (jdbc/query db ["SELECT meme_text from MEMES"]))

(defn get-rating [name realm bracket]
  (jdbc/query db ["SELECT rating FROM wow WHERE name = ? AND realm = ? AND bracket = ?" name realm bracket]))

(defn reset-wow []
  (jdbc/drop-table-ddl "wow"))

(defn rating-insertion [name realm bracket rating]
  (try
    (jdbc/insert! db :wow {:name name
                           :realm realm
                           :bracket bracket
                           :rating rating})
    (catch Exception e
      (println (.getMessage e) e)
      (println name bracket rating realm))))

(defn rating-update [name realm bracket rating]
  (jdbc/update! db :wow {:rating rating} ["name = ? AND realm = ? AND bracket = ?" name realm bracket]))