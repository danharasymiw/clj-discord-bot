(ns clj-discord-bot.core
  (:gen-class)
  (:require [clj-discord.core :as discord]
            [clj-http.client :as http-client]
            [clj-discord-bot.database :as db]
            [clj-discord-bot.commands.evangelize :as evangelize]
            [clj-discord-bot.commands.img-search :as img-search]
            [clj-discord-bot.commands.roll :as roll]
            [clj-discord-bot.commands.misc :as misc]
            [clj-discord-bot.commands.game_summon :as summon]))

(defonce discord-token (.trim (slurp "discord_token.txt")))

(defn help [type data]
  (discord/answer-command data
                          (get data "content")
                          (str "Commands"
                               (apply str (map #(str "\n" (:doc (meta %)))
                                               [#'img-search/find-img
                                                #'roll/d20
                                                #'summon/game-summon])))))

(defn game_update [type data]
  (let [server_id 0 ;server id is not returned in message data, so ignore for now ... (get-in data ["guild_id"])
        user_id (get-in data ["user" "id"])
        game_name (get-in data ["game" "name"])]
    (when (and (some? server_id)
               (some? user_id)
               (some? game_name))
      (db/game-insertion server_id
                         user_id
                         game_name))))

(defn command-mux [type data]
  (let [message (get data "content")]
    (try
      (cond
        (.contains message "clojure")
          (evangelize/get-propaganda type data)
        (.equals "!help" message)
          (help type data)
        (.equals "!d20" message)
          (roll/d20 type data)
        (.startsWith message "!summon ")
          (summon/game-summon type data)
        (.startsWith message "!img ")
          (img-search/find-img type data)
        (re-find #"(?i)ghandi" message)
          (misc/gandhi-spellcheck type data)
        (re-find #"(?i)link" message)
          (misc/links-mentioned type data))
      (catch Exception e)) ; i still dont care
    ))

(defn log-event [type data] 
  (println "\nReceived: " type " -> " data))

(defn -main [& args]
  (db/init-db)
  (discord/connect {:token discord-token
                    :functions {"MESSAGE_CREATE" [command-mux]
                                "PRESENCE_UPDATE" [game_update]
                                "ALL_OTHER" [log-event]}
                    }))
