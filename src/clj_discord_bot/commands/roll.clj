(ns clj-discord-bot.commands.roll
  (:require [clj-discord.core :as discord]))

(defn d20
  "!d20 - Picks a random number from 1-20"
  [type data]
  (discord/answer-command data
                          (get data "content")
                          (str "You rolled: " (inc (rand-int 20)))))
