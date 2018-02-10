(ns clj-discord-bot.bot-interactions.bot-discussion
  (:require [clj-discord.core :as discord]))

(defn rewrite-it-in-rust-response
  [type data]
  (let [message (get data "content")]
    (discord/post-message (get data "channel_id")
                          (str "\"I believe that those who spend their time asking people to rewrite their projects are "
                               "probably not themselves active Rust developers, as those active devs are probably busy "
                               "writing memory-safe code.\"\n"
                               "https://github.com/ansuz/RIIR"))))
