(ns sudoku.stopwatch
  (:import [java.time Instant Duration]))

(def time-counted (atom nil))

(defn start-stopwatch []
  (reset! time-counted (Instant/now)))

(defn stop-stopwatch []
  (when @time-counted
    (let [elapsed (Instant/now)
          duration (Duration/between @time-counted elapsed)]
      (.toMillis duration))))

(defn format-duration [duration-ms]
  (let [seconds (quot duration-ms 1000)
        minutes (quot seconds 60)
        hours (quot minutes 60)
        final-time (str (format "%02d" hours) ":"
                        (format "%02d" (mod minutes 60)) ":"
                        (format "%02d" (mod seconds 60)))]
    final-time))

(start-stopwatch)

(format-duration (stop-stopwatch))