(ns clj-discord-bot.common
  (:require [clj-http.client :as http-client]))

(defonce google-token (.trim (slurp "google_token.txt")))
(def img-search-url "https://www.googleapis.com/customsearch/v1?&cx=007505347843268886703%3Asukibcfg6xq")

(defn img-search [query]
  (let [result (http-client/get img-search-url
                                {:query-params {"q"          query
                                                "num"        1
                                                "searchType" "image"
                                                "start"      (+ (rand-int 100) 1)
                                                "key"        google-token},
                                 :as           :json, :debug true})]
    (get (nth (get-in result [:body :items]) 0) :link)))

(defn random-chance [max-odds]
  (if (= (inc (rand-int max-odds)) 1)
    true
    false))

(defn back-tick-it [it]
  (str "`" it "`"))

(defn bongo []
  (let [bank ["Bingo" "Bango" "Bongo" "Ba-Bingo" "Bam" "BongBingo" "Bang" "Bong" "Bing"]
        amount-words (inc (rand-int 5))
        words (take amount-words (repeatedly #(rand-nth bank)))
        delim (rand-nth ["... " ", "])]
    (str (clojure.string/join delim words) "!")))

(defn remove-command-from-message [message]
  (->> (clojure.string/split message #" ")
       (next)
       (clojure.string/join #" ")))
