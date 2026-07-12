# Connect Four – Alpha-Beta Pruning AI

A Connect Four game where an AI opponent uses the **Alpha-Beta Pruning** minimax algorithm to select its moves. Built on top of the [SAC (State-and-Action Framework)](https://github.com/iwanicki/sac) library.

---

## Files

| File | Description |
|------|-------------|
| `src/ai/connect4/connect4.java` | Core game: board state, move generation (`generateChildren`), win/draw detection, and `main()` entry point |
| `src/ai/connect4/Connect4Evaluation.java` | Board evaluation heuristic used by the AI |

---

## Algorithm

- **Alpha-Beta Pruning** (depth-limited minimax)
- Default depth limit: `5.5` — reduce to `3.0` or `4.0` on slower machines
- Player `X` = maximizer, Player `O` = minimizer

## Heuristic (`Connect4Evaluation`)

Scores a board position by summing three components:

| Component | Description |
|-----------|-------------|
| Window evaluation | Scores every 4-cell sliding window for X/O piece counts |
| Center control | Bonus for pieces in the center column (col 3) and adjacent columns |
| Height bonus | Slight preference for pieces placed lower on the board |

---

## How to Run

1. Add the SAC library `.jar` to your classpath.
2. Compile all `.java` files.
3. Run `ai.connect4.connect4`.

**Configuration** (top of `connect4.java`):
```java
public static final boolean X_PLAYER_AI = true;   // AI plays as X
public static final boolean O_PLAYER_AI = false;  // Human plays as O
```

Set both to `true` for AI vs AI, or both to `false` for human vs human.
