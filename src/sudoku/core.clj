(ns sudoku.core
  (:require [sudoku.game :refer :all])
  (:gen-class))

(defn -main []
  (play-sudoku))