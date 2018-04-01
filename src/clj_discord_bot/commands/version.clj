(ns clj-discord-bot.commands.version
  (:require [clj-discord.core :as discord]))

(def version-num "1.0.0")

(defn version
  "(version) - Specifies Bot Version"
  [type data]
  (discord/answer-command data
                          (get data "content")
                          (str "Bot is at Version - " version-num)))
