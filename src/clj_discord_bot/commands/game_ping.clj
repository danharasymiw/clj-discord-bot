(ns clj-discord-bot.commands.game-ping)

; will select the most popular game by default
(last (sort-by #(count (second %)) (group-by :game_name testy)))
