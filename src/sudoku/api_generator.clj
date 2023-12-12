(ns sudoku.api-generator)

(require '[cheshire.core :as json])
(require '[clj-http.client :as client])

(defn fetch-sudoku [difficulty]
  (let [api-url "https://sudoku-board.p.rapidapi.com/new-board"
        headers {:X-RapidAPI-Key  "49c4f4f5f5msh1c7d46e8ba824fep11da76jsnea122c26994c"
                 :X-RapidAPI-Host "sudoku-board.p.rapidapi.com"}
        query-params {:diff  difficulty
                      :stype "list"
                      :solu  "true"}
        response (client/get api-url {:headers      headers
                                      :query-params query-params})]
    (if (= 200 (:status response))
      (let [body-json (json/parse-string (:body response) true)
            solution (:solution (:response body-json))]
        solution)
      (do
        (println "Error fetching Sudoku puzzle. Status code:" (:status response))
        nil)
      )))

(defn remove-positions [solved-sudoku difficulty-level]
  (let [cells-to-remove (cond
                          (= difficulty-level "extreme easy") 5
                          (= difficulty-level "easy") 30
                          (= difficulty-level "medium") 40
                          (= difficulty-level "hard") 50)]
    (loop [puzzle solved-sudoku
           cells-removed 0]
      (if (= cells-removed cells-to-remove)
        puzzle
        (let [row (rand-int 9)
              col (rand-int 9)]
          (recur (if (= "-" (get-in puzzle [row col]))
                   puzzle
                   (assoc-in puzzle [row col] "-"))
                 (inc cells-removed)))))))

(defn get-new-generated-sudoku [difficulty-level]
  (let [sudoku (fetch-sudoku (cond
                               (= difficulty-level "extreme easy") 1
                               (= difficulty-level "easy") 1
                               (= difficulty-level "medium") 2
                               (= difficulty-level "hard") 3))]
    (remove-positions sudoku difficulty-level)))