(ns clj-discord-bot.commands.game_summon
  (:require [clj-discord-bot.database :as db]
            [clj-discord.core :as discord]))

(defn gen-pings [entries]
  (for [entry entries]
    (str "<@" (get entry :user_id) ">")))


(defn game-summon
  "!summon <part of game name> - Summons everyone who has played that game in the past!"
  [type data]
  (println (get data "content"))
  (let [message (get data "content")
        ; server_id temporary disabled, not supported by API
        results (db/game-query 0 (subs message (count "!summon ")))
        ; will select the most popular game by default
        game-info (second (last (sort-by #(count (second %)) (group-by :game_name results))))
        pings (clojure.string/join ", "(gen-pings game-info))]
    (discord/post-message (get data "channel_id")
                          (str "Summoning anyone who has played `" (get game-info :game_name) "` before...\n"
                               pings))))
