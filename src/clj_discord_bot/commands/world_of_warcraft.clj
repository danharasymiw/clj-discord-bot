(ns clj-discord-bot.commands.world-of-warcraft
  (:require [clj-discord-bot.database :as db]
            [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]
            [clj-http.client :as http-client]
            [clj-discord-bot.stackdriver :as stackdriver]))

(defonce wow-token (.trim (slurp "wow_token.txt")))

(def guys-characters [{:name "Grimoc" :realm "Cho'gall"}
                      {:name "Giblin" :realm "Cho'gall"}])

(def guys-id 218994670863122432)

(def robot-stuff-channel-id 395989933539459074)


(defn get-current-brackets [name realm]
  (let [brackets (-> (http-client/get (str "https://us.api.battle.net/wow/character/" realm "/" name
                                       "?fields=pvp&locale=en_US")
                                  {:query-params {:apikey wow-token}
                                   :as :json})
                 :body :pvp :brackets)]
    (for [bracket brackets]
      (let [bracket-info (second bracket)]
        {:bracket (:slug bracket-info)
         :rating (:rating bracket-info)}))))

(defn get-old-rating [name realm bracket]
  (let [results (db/get-rating name realm bracket)]
    (if (zero? (count results))
      (do
        (db/rating-insertion name realm bracket 0)
        0)
      (:rating (first results)))))

(defn troll-guy []
  (doseq [character guys-characters]
    (doseq [bracket (get-current-brackets (:name character) (:realm character))]
      (let [bracket-name (:bracket bracket)
            old-rating (get-old-rating (:name character) (:realm character) bracket-name)
            current-rating (:rating bracket)
            rating-diff (- current-rating old-rating)]
        (stackdriver/log (str "updating rating for " bracket " to " current-rating) :error)
        (db/rating-update (:name character) (:realm character) bracket-name current-rating)
        (when (< rating-diff 0)
          (discord/post-message robot-stuff-channel-id
                                (str (common/mention-user guys-id) "why did your rating for "
                                     bracket-name " drop by " (Math/abs rating-diff) " down to " current-rating "?\n"
                                  (common/bongo))))))))
