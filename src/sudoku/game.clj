(ns sudoku.game
  (:require [sudoku.api-generator :refer :all])
  (:require [sudoku.stopwatch :refer :all])
  (:require [sudoku.database :refer :all]))

;;function for printing the grid
(defn print-sudoku [grid initial]
  (doseq [i (range 9)]
    (when (and (zero? (rem i 3)) (not (= i 0)))
      (println (str "\u001B[0m" "=========================")))
    (doseq [j (range 9)]
      (when (zero? (rem j 3))
        (print (str "\u001B[0m" "| ")))
      (let [initial-value (get-in initial [i j])
            cell-color (if (number? initial-value)
                         "\u001B[33m"                       ; Yellow for given sudoku numbers
                         "\u001B[0m")]
        (print (str cell-color (get-in grid [i j]) " "))))
    (println (str "\u001B[0m" "| "))))

;; get difficulty from user
(defn get-input-difficulty []
  (println "Enter difficulty: extreme easy | easy | medium | hard ")
  (let [input-difficulty (read-line)]
    (cond
      (= input-difficulty "extreme easy") "extreme easy"
      (= input-difficulty "easy") "easy"
      (= input-difficulty "medium") "medium"
      (= input-difficulty "hard") "hard"
      :else (do
              (println "Invalid input. PLease try again.")
              (recur))
      )))

; get name from user
(defn get-user-name []
  (println "Enter your name: ")
  (let [name (read-line)]
    (println "Your score will be assigned to name: " name)
    name))


(defn valid-user-input? [user-input]
  (let [input-parts (map #(try (Integer. %) (catch Exception e nil)) (clojure.string/split user-input #" "))]
    (and (= 3 (count input-parts)) (every? integer? input-parts))))

;; VALIDATIONS for entered number, row, column
(defn valid-number-entered? [num]
  (and (>= num 1) (<= num 9)))

(defn valid-position? [i]
  (and (>= i 0) (<= i 8)))

;; GAME WON
(defn sudoku-filled? [sudoku]
  (every? (fn [row] (every? #(not= "-" %) row)) sudoku))

;; MOVE CORRECT ?
(defn position-empty? [row col grid]
  (= (get-in grid [row col]) "-"))

(defn row-free-of-num? [num row grid]
  (not (some #(= num %) (nth grid row))))

(defn column-free-of-num? [num col grid]
  (not (some #(= num %) (map #(nth % col) grid))))

(defn block-free-of-num? [num row col grid]
  (let [block-row-start (* 3 (quot row 3))
        block-col-start (* 3 (quot col 3))]
    (not (some #(= num %)
           (flatten                                         ;;better flatten than apply concat
             (for [i (range block-row-start (+ block-row-start 3))]
               (for [j (range block-col-start (+ block-col-start 3))]
                 (get-in grid [i j]))))))))


(defn sudoku-rules-valid? [num row col grid]
  (and (position-empty? row col grid)
       (row-free-of-num? num row grid)
       (column-free-of-num? num col grid)
       (block-free-of-num? num row col grid)))

;; backtracking algorithm - solver for checking whether a move is correct
(defn get-empty-position-dash [grid]
  (some (fn [row]
          (some (fn [col]
                  (when (position-empty? row col grid)
                    [row col]))
            (range (count (grid row)))))
    (range (count grid))))

(defn solver-dash [board]
  (if (not (some empty? board))
    board
    (let [[i j] (get-empty-position-dash board)]
      (loop [num 1]
        (if (<= num 9)
          (if (sudoku-rules-valid? board i j num)
            (if-let [new-board (assoc-in board [i j] num)]
              (if-let [result (solver-dash new-board)]
                result
                (recur (inc num)))
              (recur (inc num)))
            (recur (inc num)))
          nil)))))

(defn move-correct [num row col grid]
  (if (sudoku-rules-valid? num row col grid)
    (solver-dash grid)
    nil))


(defn play-sudoku []
  (println "Welcone to Sudoku! Let's play!")
  (let [name (get-user-name)
        difficulty-level (get-input-difficulty)
        initial-generated-sudoku (get-new-generated-sudoku difficulty-level)]
    (start-stopwatch)
    (loop [sudoku initial-generated-sudoku]
      (println "Current sudoku:")
      (print-sudoku sudoku initial-generated-sudoku)
      (println "Enter a number (1 - 9) followed by it's position in the matrix [ex. 5 1 9]")
      (println "Or enter 'q' to quit the game.")
      (let [user-input (read-line)]
        (if (= user-input "q")
          ;; TRUE: user iput = q
          (println "Exiting game")
          ;; FALSE: numbers entered
          (if (valid-user-input? user-input)
            ; TRUE: valid number of arguments
            (let [[num r c] (map #(Integer. %) (clojure.string/split user-input #" "))
                  row (dec r)
                  col (dec c)]
              ;;VALIDATION OF A MOVE
              (if (and (valid-number-entered? num) (valid-position? row) (valid-position? col))
                ;;TRUE: valid numbers
                (do
                  (if (move-correct num row col sudoku)
                    ;;TRUE: MOVE CORRECT
                    (let [new-board (assoc-in sudoku [row col] num)]
                      (println (str "\u001B[32m" "Bravoo! Correct move!"))
                      (if (sudoku-filled? new-board)
                        ;;TRUE: All postions are filled
                        (do
                          (let [time-elapsed (format-duration (stop-stopwatch))]
                            (println (str "\u001B[34m" "You're time is: " time-elapsed))
                            (println (str "\u001B[34m" "Congratulations! You have solved Sudoku!"))
                            (save-game name difficulty-level time-elapsed)
                            (print-sudoku new-board initial-generated-sudoku)

                            ((fn mini-menu []
                               (println "Mini menu:")
                               (println "1 - See rankings by your difficulty")
                               (println "2 - See rankings by all difficulties")
                               (println "3 - Play new game")
                               (println "4 - Exit game")
                               (let [input (read-line)]
                                 (cond
                                   (= input "1") (do
                                                   (print-rankings-by-difficulty difficulty-level)
                                                   (recur))
                                   (= input "2") (do
                                                   (print-all-rankings)
                                                   (recur))
                                   (= input "3") (do
                                                   (play-sudoku)
                                                   (recur))
                                   (= input "4") (do
                                                   (println "Existing game.")
                                                   (System/exit 0))
                                   :else (do
                                           (println (str "\u001B[35m" "Invalid input... Please enter one of four option."))
                                           (recur))))))))
                        ;;FALSE: Continue game
                        (recur new-board)))
                    ;;FALSE MOVE INCORRECT
                    (do
                      (println (str "\u001B[31m" "Move incorrect....."))
                      (recur sudoku))))
                ;;FALSE invalid numbers, try again
                (do
                  (println (str "\u001B[35m" "Bad input..a. Please ty again..."))
                  (recur sudoku))))
            ; FALSE: wrong number of arguments or not all integers
            (do
              (println (str "\u001B[35m" "Invalid arguments..."))
              (println (str "\u001B[35m" "Please enter 3 integers.. [number row column] 5 1 9"))
              (recur sudoku))
            ))))))