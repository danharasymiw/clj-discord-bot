(ns clj-discord-bot.common
  (:require [clj-http.client :as http-client]))

(defonce google-token (.trim (slurp "google_token.txt")))
(def img-search-url "https://www.googleapis.com/customsearch/v1?&cx=007505347843268886703%3Asukibcfg6xq")


(defn img-search [query]
  (let [result (http-client/get img-search-url {:query-params {"q"          query
                                                                 "num"        1
                                                                 "searchType" "image"
                                                                 "start"      (+ (rand-int 100) 1)
                                                                 "key"        google-token},
                                                  :as           :json, :debug true})]
    (get (nth (get-in result [:body :items]) 0) :link)))