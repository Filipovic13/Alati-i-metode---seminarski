(ns sudoku.core
  (:gen-class))

;; initial sudoku grid
;; map with sudoku matrices
(def grids {:easy  [["-" "-" "-" 2 6 "-" 7 "-" 1]
                    [6 8 "-" "-" 7 "-" "-" 9 "-"]
                    [1 9 "-" "-" "-" 4 5 "-" "-"]
                    [8 2 "-" 1 "-" "-" "-" 4 "-"]
                    ["-" "-" 4 6 "-" 2 9 "-" "-"]
                    ["-" 5 "-" "-" "-" 3 "-" 2 8]
                    ["-" "-" 9 3 "-" "-" "-" 7 "-"]
                    ["-" 4 "-" "-" 5 "-" "-" 3 "-"]
                    [7 "-" 3 "-" 1 8 "-" "-" "-"]]

            :medium  [["-" 2 "-" 6 "-" 8 "-" "-" "-"]
                      [5 8 "-" "-" "-" 9 7 "-" "-"]
                      ["-" "-" "-" "-" 4 "-" "-" "-" "-"]
                      [3 7 "-" "-" "-" "-" 5 "-" "-"]
                      [6 "-" "-" "-" "-" "-" "-" "-" 4]
                      ["-" "-" 8 "-" "-" "-" "-" 1 3]
                      ["-" "-" "-" "-" 2 "-" "-" "-" "-"]
                      ["-" "-" 9 8 "-" "-" "-" 3 6]
                      ["-" "-" "-" 3 "-" 6 "-" 9 "-"]]

            :hard [["-" "-" "-" 2 6 "-" 7 "-" 1]
                   [6 8 "-" "-" 7 "-" "-" 9 "-"]
                   [1 9 "-" "-" "-" 4 5 "-" "-"]
                   [8 2 "-" 1 "-" "-" "-" 4 "-"]
                   ["-" "-" 4 6 "-" 2 9 "-" "-"]
                   ["-" 5 "-" "-" "-" 3 "-" 2 8]
                   ["-" "-" 9 3 "-" "-" "-" 7 "-"]
                   ["-" 4 "-" "-" 5 "-" "-" 3 "-"]
                   [7 "-" 3 "-" 1 8 "-" "-" "-"]] })

;;function for printing the grid
(defn print-sudoku [grid]
  (doseq [i (range 9)]
    (when (and (zero? (rem i 3)) (not (= i 0)) )
      (println "=================================="))
    (doseq [j (range 9)]
      (when (zero? (rem j 3))
        (print "| "))
      (print (get-in grid [i j]) " "))
    (println "| ")))

;; get difficulty from user
(defn get-input-difficulty []
  (println "Enter difficulty: easy | medium | hard ")
  (let [input-diff (read-line)]
    (cond
      (= input-diff "easy") (get grids :easy)
      (= input-diff "medium") (get grids :medium)
      (= input-diff "hard") (get grids :hard)
      :else (do
              (println "Invalid input. PLease try again.")
              (recur))
      )))

;; VALIDATIONS
(defn valid-number-entered? [num]
  (and (>= num 1) (<= num 9)))

(defn valid-position? [i]
  (and (>= i 0) (<= i 8) ))


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
           (apply concat
                  (for [i (range block-row-start (+ block-row-start 3))]
                    (for [j (range block-col-start (+ block-col-start 3))]
                      (get-in grid [i j]))))))))


(defn sudoku-rules-valid? [num row col grid]
  (and (not (some #(= num %) (get-in grid [row])))
       (not (some #(= num %) (map #(get % col) grid)))
       (not (some #(= num %) (for [i (range 3) j (range 3)] (get-in grid [(+ (* 3 (quot row 3)) i) (+ (* 3 (quot col 3)) j)]))))))

(defn get-empty-position [grid]
  (some (fn [row]
          (some (fn [col]
                  (when (position-empty? row col grid)
                    [row col]))
            (range (count (grid row)))))
    (range (count grid))))

;; backtracking algorithm
(defn solver [board]
  (if (not (some empty? board))
    board
    (let [[i j] (get-empty-position board)]
      (loop [num 1]
        (if (<= num 9)
          (if (sudoku-rules-valid? board i j num)
            (if-let [new-board (assoc-in board [i j] num)]
              (if-let [result (solver new-board)]
                result
                (recur (inc num)))
              (recur (inc num)))
            (recur (inc num)))
          nil)))))


(defn move-correct? [num row col grid]
  (if (and (position-empty? row col grid)
           (row-free-of-num? num row grid)
           (column-free-of-num? num col grid)
           (block-free-of-num? num row col grid))
    (solver grid)
    (= 0 1)))

(defn play-sudoku []
  (println "Welcone to Sudoku! Let's play!")
  (let [board-input (get-input-difficulty)]
  (loop [sudoku board-input]
    (println "Current sudoku:")
    (print-sudoku sudoku)
    (println "Enter a number (1 - 9) followed by it's position in the matrix [ex. 5 0 2]")
    (println "Or enter 'q' to quit the game.")
    (let [user-input (read-line)]
      (if (= user-input "q")
        ;; TRUE: user iput = q
        (println "Exiting game")
        ;; FALSE: numbers entered
        (let [ [num row col] (map #(Integer. %) (clojure.string/split user-input #" ") )]
          ;;VALIDATION OF A MOVE
          (if (and (valid-number-entered? num) (valid-position? row) (valid-position? col)  )
            ;;TRUE: valid numbers
            (do
              (if (move-correct? num row col sudoku)
                ;;TRUE: MOVE CORRECT
                (let [new-board (assoc-in sudoku [row col] num)]
                  (println "Bravoo! Correct move!")
                  (if (sudoku-filled? new-board)
                    ;;TRUE: All postions are filled
                    (do
                      (println "Congratulations! You have solved Sudoku!")
                      (print-sudoku new-board)
                      (println "Existing game. Bye!"))
                    ;;FALSE: Continue game
                    (recur new-board)))
                ;;FALSE MOVE INCORRECT
                (do
                  (println "Move incorrect.....")
                  (recur sudoku))))
            ;;FALSE invalid numbers, try again
            (do
              (println "Bad input... Please ty again...")
              (recur sudoku)))))))))

  (defn -main []
    (play-sudoku))