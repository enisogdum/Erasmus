# Sliding Puzzle Solver – A* Search

Solves n×n sliding tile puzzles using the **A\* algorithm** with two admissible heuristics. Built on the [SAC (State-and-Action Framework)](https://github.com/iwanicki/sac) library.

---

## Files

| File | Description |
|------|-------------|
| `src/ai/SlidingPuzzle/SliddingPuzzle.java` | Puzzle board state, shuffle logic, and child generation |
| `src/ai/SlidingPuzzle/Main.java` | Experiment runner — runs & compares heuristics |
| `src/ai/SlidingPuzzle/ManhattanDistanceHeuristic.java` | Manhattan Distance heuristic |
| `src/ai/SlidingPuzzle/MisplacedTilesHeuristic.java` | Misplaced Tiles heuristic |

---

## Experiments

The `Main` class runs two experiments and prints performance statistics:

| Experiment | Grid Size | Puzzles | Mixing Moves |
|------------|-----------|---------|--------------|
| 1 | 3×3 | 100 | 30 |
| 2 | 4×4 | 10 | 30 |

For each experiment both heuristics are tested and the following averages are reported:
- Time (ms)
- Closed states
- Open states
- Path length (solution depth)

---

## Heuristics

| Heuristic | Description | Admissible? |
|-----------|-------------|-------------|
| Manhattan Distance | Sum of L1 distances of each tile to its goal position | ✅ Yes |
| Misplaced Tiles | Count of tiles not in their goal position | ✅ Yes |

Manhattan Distance is a stronger (more informed) heuristic and typically requires fewer expanded nodes.

---

## How to Run

1. Add the SAC library `.jar` to your classpath.
2. Compile all `.java` files.
3. Run `ai.SlidingPuzzle.Main`.
