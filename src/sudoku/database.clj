(ns sudoku.database
  (:require [clojure.java.jdbc :as jdbc]))

;; formatting date
(import [java.text SimpleDateFormat])

(defn format-date-to-string [date]
  (let [formatter (SimpleDateFormat. "HH:mm:ss")]
    (.format formatter date)))


;;db specifications
(def mysql-db {:dbtype   "mysql"
               :dbname   "clojure_project"
               :user     "root"
               :password "root"})

; save won game data in db
(defn save-game [player-name difficulty time-elapsed]
  (jdbc/insert! mysql-db :games {:player_name  player-name
                                 :difficulty   difficulty
                                 :time_elapsed time-elapsed}))


; get results only by difficulty player chosen
(defn get-rankings-by-difficulty [difficulty]
  (let [db-results (jdbc/query mysql-db ["SELECT player_name, difficulty, time_elapsed
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
  (let [db-results (jdbc/query mysql-db ["SELECT player_name, difficulty, time_elapsed
                                          FROM games
                                          ORDER BY difficulty, time_elapsed ASC"])
        formatted-result (map (fn [i]
                                (assoc i :time_elapsed (format-date-to-string (:time_elapsed i))))
                              db-results)]
    formatted-result))

(defn custom-order [data]
  (zipmap ["extreme easy" "easy" "medium" "hard"]
          (map #(get data %) ["extreme easy" "easy" "medium" "hard"])))

(defn group-by-difficulty [results]
  (custom-order (group-by :difficulty results)))

(defn print-all-rankings []
  (let [formatted-results (group-by-difficulty (get-all-rankings))]
    (doseq [[difficulty players] formatted-results]
      (println (str "Difficulty: " difficulty))
      (doseq [[index player] (map-indexed vector players)]
        (println (str "  " (inc index) ". " (:player_name player) " " (:time_elapsed player))))
      (println))))



