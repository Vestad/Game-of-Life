# Game of Life
Project written in Java based on [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).

Solved using macOS and Java 16.

## About the project
### Rules
1. Any live cell with two or three live neighbours survives.
2. Any dead cell with three live neighbours becomes a live cell.
3. All other live cells die in the next generation. Similarly, all other dead cells stay dead.

### First generation
The first generation is created using a seed of random population size within the bounds of the game units allowed by the board.

### Usage
```javac *.java && java GameOfLife```
