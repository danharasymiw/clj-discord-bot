(ns clj-discord-bot.commands.game_summon
  (:require [clj-discord-bot.database :as db]
            [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]))

(defn gen-pings [entries]
  (for [entry entries]
    (str "<@" (get entry :user_id) ">")))

(defn game-summon
  "!summon <part of game name> - Summons everyone who has played that game in the past!"
  [type data]
  (let [message (get data "content")
        ; server_id temporary disabled, not supported by API
        results (db/game-query 0 (subs message (count "!summon ")))
        ; will select the most popular game by default
        game-info (second (last (sort-by #(count (second %)) (group-by :game_name results))))
        pings (clojure.string/join " " (gen-pings game-info))
        game-name (:game_name (first game-info))]
    (discord/post-message (get data "channel_id")
                          (if (empty? game-name)
                            (str "No one has ever played any game containing that string pattern\n\n"
                                 (common/bongo))
                            (str "Summoning anyone who has played `" game-name "` before...\n"
                                 pings "\n\n"
                                 (common/bongo))))))

(defn game-list
  "!gamelist <@ mention user> - Prints all games associated with that user"
  [type data]
  (let [id (get (first (get data "mentions")) "id")
        results (db/get-users-games id)
        games (clojure.string/join "\n" (map common/back-tick-it (map :game_name results)))]
    (println games)
    (discord/post-message (get data "channel_id")
                          (str "<@" id "> has played the following games / streamed with the following titles in the past:\n"
                               games))))

(defn game-add
  "!gameadd <@ mention user> <game name> - Adds someones game entry because they dont want to enable game status"
  [type data]
  (let [id (get (first (get data "mentions")) "id")
        message (get data "content")
        game-name (nth (clojure.string/split message #" ") 2)]
    (db/game-insertion 0 id game-name)
    (discord/post-message (get data "channel_id")
                          (common/bongo))))

