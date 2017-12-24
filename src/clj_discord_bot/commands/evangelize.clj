(ns clj-discord-bot.commands.evangelize)

(defonce lib (keys (ns-publics 'clojure.core)))
(defonce adjectives (clojure.string/split-lines (slurp "assets/adjectives")))

(defn docstring [fn-name]
  (:doc (meta (resolve (symbol fn-name)))))

(defn get-propaganda []
  (let [fn-name (rand-nth lib)]
    (str "I see you are talking about Clojure.  Clojure is a very " (rand-nth adjectives) " language!\n"
         "Did you know Clojure has a function called `" fn-name "`?\n"
         "```\n"
         (docstring fn-name) "\n"
         "```\n"
         "http://clojuredocs.org/clojure.core/" fn-name
    )))
