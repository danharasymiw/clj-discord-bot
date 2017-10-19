(ns clj-discord-bot.core
  (:gen-class)
  (:require [clj-discord.core :as discord]
            [clj-http.client :as http-client]))

(defonce discord-token (.trim (slurp "discord_token.txt")))
(defonce google-token (.trim (slurp "google_token.txt")))
(defonce quaggan-token (.trim (slurp "quaggan_joe.txt")))

(defn img-search-url [] "https://www.googleapis.com/customsearch/v1?&cx=007505347843268886703%3Asukibcfg6xq")


(defn d20
      "!d20 - Picks a random number from 1-20"[type data]
      (discord/answer-command data "!d20" (str "You rolled: " (+ (rand-int 100) 1))))

(defn img-search [query]
      (let [result (http-client/get (img-search-url) {:query-params {"q" query
                                                                     "num" 1
                                                                     "searchType" "image"
                                                                     "start" (+ (rand-int 100) 1)
                                                                     "key" google-token},
                                                      :as :json, :debug true})]
           (get (nth (get-in result [:body :items]) 0) :link)))

(defn find-img
      "!img <your_query> - Finds a random image of <your_query>"
      [type data]

      (let [message (get data "content")]
           (if (.startsWith message "!img ")
             (discord/answer-command data message (img-search (subs message (count "!img ")))))))
(defn quaggan-answer [data message]
      (Thread/sleep (* (+ (rand-int 5) 1) 60 1000))
      (discord/answer-command data message (img-search "quaggan")))

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
      (let [message (get data "content")]
           (if (re-find #"(?i)ghandi" message)
             (discord/answer-command data message "Gandhi  (╯°□°）╯︵ ┻━┻"))))

(defn links-mentioned [type data]
      (let [message (get data "content")]
           (if (re-find #"(?i)link" message)
             (discord/answer-command data message (img-search "lynx")))))

(defn help [type data]
      (let [message (get data "content")]
           (print "helpin")
           (if (.equals "!help" message)
             (discord/answer-command data message
                                     (str "Commands" (apply str
                                          (map #(str "\n" (:doc (meta %))) [#'find-img
                                                                            #'d20
                                                                            #'help])))))))


(defn log-event [type data] 
  (println "\nReceived: " type " -> " data))

(defn -main [& args]
  (discord/connect discord-token
                   {"MESSAGE_CREATE" [d20
                                      find-img
                                      quaggan-joe
                                      gandhi-spellcheck
                                      links-mentioned
                                      help]
                    ; "ALL_OTHER" [log-event]
                    }
                   true))

;(discord/disconnect)