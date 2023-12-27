(ns sudoku.database
  (:require [clojure.java.jdbc :as jdbc]))

;; formatting date
(import [java.text SimpleDateFormat])

(defn format-date-to-string [date]
  (let [formatter (SimpleDateFormat. "HH:mm:ss")]
    (.format formatter date)))


;;db specifications
(def h2-db {:classname   "org.h2.Driver"
            :subprotocol "h2:mem"
            :subname     "sudoku_project;DB_CLOSE_DELAY=-1"
            :user        "sa"
            :password    ""})

(defn execute-statements [statements]
  (jdbc/with-db-transaction [tx-conn h2-db]
                            (jdbc/db-do-commands tx-conn (doall statements))))



(let [sql-statements ["DROP TABLE IF EXISTS games;",
                      "CREATE TABLE games (id BIGINT NOT NULL AUTO_INCREMENT, player_name VARCHAR(255), difficulty VARCHAR(255), time_elapsed TIME, PRIMARY KEY (id));",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Mika', 'easy', TIME '00:03:21');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Zika', 'easy', TIME '00:04:01');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Tika', 'easy', TIME '00:03:10');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Kiza', 'medium', TIME '00:05:23');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Misa', 'medium', TIME '00:06:02');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Daca', 'medium', TIME '00:05:01');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Pera', 'hard', TIME '00:05:01');",
                      "INSERT INTO games (player_name, difficulty, time_elapsed) VALUES ('Daki', 'hard', TIME '00:06:33');"]]
  (execute-statements sql-statements))

; save won game data in db
(defn save-game [player-name difficulty time-elapsed]
  (jdbc/insert! h2-db :games {:player_name  player-name
                              :difficulty   difficulty
                              :time_elapsed time-elapsed}))


; get results only by difficulty player chosen
(defn get-rankings-by-difficulty [difficulty]
  (let [db-results (jdbc/query h2-db ["SELECT player_name, difficulty, time_elapsed
                                          FROM games WHERE difficulty = ?
                                          ORDER BY time_elapsed ASC" difficulty])
        formatted-results (map (fn [i]
                                 (assoc i :time_elapsed (format-date-to-string (:time_elapsed i))))
                               db-results)]
    formatted-results))

(defn print-rankings-by-difficulty [difficulty]
  (let [players (get-rankings-by-difficulty difficulty)]
    (println (str "Difficulty: " difficulty))
    (doseq [player players]
      (println (str " " (:player_name player) " " (:time_elapsed player)))
      )))

; get results for all difficulties
(defn get-all-rankings []
  (let [db-results (jdbc/query h2-db ["SELECT player_name, difficulty, time_elapsed
                                          FROM games
                                          ORDER BY difficulty, time_elapsed ASC"])
        formatted-result (map (fn [i]
                                (assoc i :time_elapsed (format-date-to-string (:time_elapsed i))))
                              db-results)]
    formatted-result))


(defn group-by-difficulty [results]
  (group-by :difficulty results))

(def ordered-difficulties ["extreme easy" "easy" "medium" "hard"])

(defn custom-order [data]
  (select-keys data ordered-difficulties))

(defn print-all-rankings []
  (let [formatted-results (custom-order (group-by-difficulty (get-all-rankings)))]
    (doseq [[difficulty players] formatted-results]
      (println (str "Difficulty: " difficulty))
      (doseq [[index player] (map-indexed vector players)]
        (println (str "  " (inc index) ". " (:player_name player) " " (:time_elapsed player))))
      (println))))