(ns clj-discord-bot.commands.evangelize
  (:require [clj-discord.core :as discord]))

(defonce lib (keys (ns-publics 'clojure.core)))
(defonce adjectives (clojure.string/split-lines (slurp "assets/adjectives")))

(defn docstring [fn-name]
  (:doc (meta (resolve (symbol fn-name)))))

(defn get-propaganda [type data]
  (discord/answer-command data
                          (get data "content")
                          (let [fn-name (rand-nth lib)
                                adjective (rand-nth adjectives)]
                            (str "I see you are talking about Clojure.  Clojure is a very " adjective " language!\n"
                                 "Did you know Clojure has a function called `" fn-name "`?\n"
                                 "```\n"
                                 (docstring fn-name) "\n"
                                 "```\n"
                                 "http://clojuredocs.org/clojure.core/" fn-name))))
