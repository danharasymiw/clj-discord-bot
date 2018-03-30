(ns clj-discord-bot.commands.game_summon
  (:require [clj-discord-bot.database :as db]
            [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]
            [clj-http.client :as http-client]))

(defonce steam-token (slurp "steam_token.txt"))

(defn gen-pings [entries]
  (for [entry entries]
    (str (common/mention-user (get entry :user_id)))))

(defn game-summon
  "(summon <part of game name>) - Summons everyone who has played that game in the past"
  [type data]
  (let [message (get data "content")
        ; server_id temporary disabled, not supported by API
        results (db/game-query 0 (subs message (count "summon ")))
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
  "(gamelist <@ mention user(s)>) - Prints all games associated with that user, or all the games those users have in common "
  [type data]
  (let [channel_id (get data "channel_id")
        mention-ids (map #(get % "id") (get data "mentions"))
        results (for [id mention-ids]
                  (into #{} (map :game_name (db/get-users-games id))))
        common-games (apply clojure.set/intersection results)
        backticked-games (map common/back-tick-it common-games)]
    (discord/post-message (get data "channel_id")
                          (str
                           (apply str (for [id mention-ids]
                                        (common/mention-user id)))
                           (if (> (count mention-ids) 1)
                             "have both "
                             "has ")
                           "played the following games / streamed with the following titles in the past:\n"))

    (doseq [games (partition-all 10 backticked-games)]
      (Thread/sleep 1000)
      (discord/post-message channel_id
                            (clojure.string/join "\n" games)))))

(defn game-add
  "(gameadd <@ mention user(s)> <game name>) - Adds someones game entry because they dont want to enable game status"
  [type data]
  (let [mention-ids (map #(get % "id") (get data "mentions"))
        message (get data "content")
        game-name (->> (clojure.string/split message #" ")
                       (drop (inc (count mention-ids)))
                       (clojure.string/join #" "))]
    (doseq [id mention-ids]
      (db/game-insertion 0 id game-name))
    (discord/post-message (get data "channel_id")
                          (common/bongo))))

(defn game-remove
  "(gameremove <@ mention user(s) <game name>) - Removes a game entry from someone"
  [type data]
  (let [mention-ids (map #(get % "id") (get data "mentions"))
        message (get data "content")
        game-name (->> (clojure.string/split message #" ")
                       (drop (inc (count mention-ids)))
                       (clojure.string/join #" "))]
    (doseq [id mention-ids]
      (db/game-deletion 0 id game-name))
    (discord/post-message (get data "channel_id")
                          (common/bongo))))

(defn add-steam-games
  "(steamadd <@ mention user> <steam community profile url>) - Adds all of the games from someones steam library to that person"
  [type data]
  (let [mention-id (first (map #(get % "id") (get data "mentions")))
        message (get data "content")
        steam-name (-> (clojure.string/split message #"/")
                       (last))
        steam-id (-> (http-client/get "http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001"
                                      {:query-params {:key steam-token
                                                      :vanityurl steam-name}
                                       :as :json})
                     :body :response :steamid)
        games (-> (http-client/get "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/"
                                   {:query-params {:key steam-token
                                                   :steamid steam-id
                                                   :include_played_free_games 1
                                                   :include_appinfo 1} :as :json})
                  :body :response :games)]
    (doseq [game games]
      (println (:name game))
      (db/game-insertion 0 mention-id (:name game)))
    (discord/post-message (get data "channel_id")
                          (str (common/bongo) "\n"
                               "Added " (count games) " games to user."))))
