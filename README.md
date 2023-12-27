# Clojure Sudoku Game

Welcome to the Clojure Sudoku Game! This text-based Sudoku implementation is crafted in Clojure using Leiningen and designed to be played through the terminal. Challenge yourself with puzzles of varying difficulty levels and track your scores.

## Gameplay

When you start the game, you'll be prompted to enter your name:

```
Welcome to Sudoku! Let's play!
Enter your name: your-name
```

After entering your name (to which your score will be assigned), the next prompt will be displayed for you to choose the difficulty level. There are four difficulty levels:

-  Extreme Easy (5 positions empty for testing)
-  Easy (25 positions empty)
-  Medium (35 positions empty)
-  Hard (50 positions empty)

After entering the difficulty level, please wait for a moment. Subsequently, a Sudoku grid will be presented:

```
Current sudoku:
| 1 - 8 | - 3 6 | 5 7 9 |
| 3 - 6 | - 5 9 | 2 1 8 |
| - 7 9 | - 2 - | 3 4 - |
=========================
| 2 3 7 | 5 6 1 | 8 9 4 |
| 4 8 1 | - 9 - | 6 5 7 |
| - - 5 | 8 7 4 | 1 2 - |
=========================
| - - 2 | 9 8 - | 4 6 5 |
| 9 6 - | 2 4 - | - 8 1 |
| 8 - 4 | 6 1 7 | - 3 2 |
Enter a number (1 - 9) followed by it's position in the matrix [ex. 5 1 9]
Or enter 'q' to quit the game.
```

Dashes represent positions that need to be filled. If you don't enter a valid input, a message will be displayed:

```
Bad input... Please try again...
```

If the player enters a number in the correct position, they will receive a message:

```
Bravo! Correct move!
```

If the move is incorrect:

```
Move incorrect.....
```

Upon winning the game, a message will be shown with the time taken:

```
Your time is: 00:01:47
Congratulations! You have solved Sudoku!
```

After finishing the game, players can choose from the following options:

-  See rankings by difficulty in which they just played.
-  See rankings by all difficulties.
-  Play a new game.
-  Exit the game.

```
Mini menu:
1 - See rankings by your difficulty
2 - See rankings by all difficulties
3 - Play new game
4 - Exit game
```

When the second option is selected, the player will see the rankings of players that are, by default, stored in the database.

```
Difficulty: easy
  1. Tika 00:03:10
  2. Mika 00:03:21
  3. Zika 00:04:01

Difficulty: medium
  1. Daca 00:05:01
  2. Kiza 00:05:23
  3. Misa 00:06:02

Difficulty: hard
  1. Pera 00:05:01
  2. Daki 00:06:33
```
