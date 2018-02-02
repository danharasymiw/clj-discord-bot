(ns clj-discord-bot.commands.misc
  (:require [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]))

(defn gandhi-spellcheck [type data]
  (discord/answer-command data
                          (get data "content")
                          "Gandhi  (╯°□°）╯︵ ┻━┻"))

(defn links-mentioned [type data]
  (discord/answer-command data
                          (get data "content")
                          (common/img-search "lynx")))