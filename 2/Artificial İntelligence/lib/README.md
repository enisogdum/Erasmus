# lib/ — SAC Library Dependencies

This folder contains the compiled JAR files required to build and run the Java projects in this repository.

## Contents

| File | Description |
|------|-------------|
| `sac-1.0.3.jar` | **SAC (State-and-Action Framework)** — core AI search library used by all Java projects |
| `jcommon-1.0.17.jar` | JFreeChart dependency (charting utility) |
| `jfreechart-1.0.14.jar` | JFreeChart — used by SAC's stats/visualization module |
| `swt.jar` | SWT (Standard Widget Toolkit) — used by SAC's graphical monitor |
| `sac-1.0.3-uml.pdf` | UML class diagram of the SAC library architecture |

## How to Use (Compiling)

Add all JARs to your classpath when compiling:

```bash
javac -cp "lib/*" src/*.java
```

Or in Eclipse: right-click project → Build Path → Add External JARs → select all files in `lib/`.

## SAC Library

SAC provides ready-made implementations of:
- **Graph search:** A\*, Best-First, BFS, DFS, Dijkstra, IDA\*
- **Game search:** Minimax, Alpha-Beta Pruning, Scout
- **Abstractions:** `GraphState`, `GameState`, `StateFunction` (heuristic)

Projects using SAC: `ai.connect4`, `ai.SlidingPuzzle`, `ai.sudoku`
