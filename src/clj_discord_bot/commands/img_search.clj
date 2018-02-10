(ns clj-discord-bot.commands.img-search
  (:require [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]))

(defn find-img
  "(img <your_query>) - Finds a random image of <your_query>"
  [type data]
  (let [message (get data "content")]
    (discord/answer-command data
                            message
                            (common/img-search (common/remove-command-from-message message)))))
