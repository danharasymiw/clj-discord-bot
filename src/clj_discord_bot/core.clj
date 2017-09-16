(ns clj-discord-bot.core
  (:gen-class)
  (:require [clj-discord.core :as discord]
            [clj-http.client :as http-client]))

(defonce discord-token (.trim (slurp "discord_token.txt")))
(defonce google-token (.trim (slurp "google_token.txt")))
(defonce quaggan-token (.trim (slurp "quaggan_joe.txt")))

(defn generate-img-search-url [query] (str "https://www.googleapis.com/customsearch/v1?q=" query "&cx=007505347843268886703%3Asukibcfg6xq&num=1&searchType=image&start=" (+ (rand-int 100) 1)  "&key=" google-token))

(defn d20
      "!d20 - Picks a random number from 1-20"[type data]
      (discord/answer-command data "!d20" (str "You rolled: " (+ (rand-int 100) 1))))

(defn img-search [query]
      (let [result (http-client/get (generate-img-search-url query) {:as :json})]
           (get (nth (get-in result [:body :items]) 0) :link)))

(defn find-img
      "!img <your_query> - Finds a random image of <your_query>"
      [type data]

      (let [message (get data "content")]
           (if (.startsWith message "!img")
             (let [split-string (clojure.string/split message #" ")]
                  (if (> (count split-string) 1)
                    (discord/answer-command data message (img-search (nth split-string 1))))))))

(defn quaggan-answer [data message]
      (Thread/sleep (* (+ (rand-int 5) 1) 60 1000))
      (discord/answer-command data message (img-search "quaggan")))

;(defn quaggan-joe [type data]
;      (let [message (get data "content")
;            mentions (get data "mentions")
;            mention-all (get data "mention_everyone")]
;
;           (if (or mention-all (.contains message quaggan-token))
;             (quaggan-answer data message))))

(defn gandhi-spellcheck [type data]
      (let [message (get data "content")]
           (if (re-find #"(?i)ghandi" message)
             (discord/answer-command data message "Gandhi  (╯°□°）╯︵ ┻━┻"))))

(defn help [type data]
      (let [message (get data "content")]
           (print "helpin")
           (if (.equals "!help" message)
             (discord/answer-command data message
                                     (str "Commands \n"
                                          (:doc (meta #'d20)) "\n"
                                          (:doc (meta #'find-img)) "\n"
                                          (:doc (meta #'help)))))))


(defn log-event [type data] 
  (println "\nReceived: " type " -> " data))

(defn -main [& args]
  (discord/connect discord-token
                   {"MESSAGE_CREATE" [d20 find-img gandhi-spellcheck help]
                    "MESSAGE_UPDATE" [d20]
                    ; "ALL_OTHER" [log-event]
                    }
                   true))

;(discord/disconnect)