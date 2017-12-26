(ns clj-discord-bot.commands.misc
  (:require [clj-discord.core :as discord]
            [clj-discord-bot.common :as common]))

(defonce quaggan-token (.trim (slurp "quaggan_joe.txt")))

(defn quaggan-answer [data message]
  (Thread/sleep (* (+ (rand-int 5) 1) 60 1000))
  (discord/answer-command data message (common/img-search "quaggan")))

; in flux, disabled at the moment
(defn quaggan-joe [type data]
  (let [message (get data "content")
        mentions (get data "mentions")
        mention-all (get data "mention_everyone")]
    (println "THIS IS A TEST")
    (println message)
    (println quaggan-token)
    (if (.contains message quaggan-token)
      "test")))

(defn gandhi-spellcheck [type data]
  (discord/answer-command data
                          (get data "content")
                          "Gandhi  (╯°□°）╯︵ ┻━┻"))

(defn links-mentioned [type data]
  (discord/answer-command data
                          (get data "content")
                          (common/img-search "lynx")))