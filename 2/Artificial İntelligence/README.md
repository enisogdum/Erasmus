# Artificial Intelligence – Course Projects

Practical projects and exercises from the **Artificial Intelligence** course.  
Topics covered: classical search, game-playing AI, and machine learning classifiers.

---

## 📁 Project Structure

```
.
├── lib/                   # SAC library JARs (required for Java projects)
│   ├── sac-1.0.3.jar
│   ├── jcommon-1.0.17.jar
│   ├── jfreechart-1.0.14.jar
│   ├── swt.jar
│   └── sac-1.0.3-uml.pdf
│
├── ai.connect4/           # Connect Four with Alpha-Beta Pruning AI
│   ├── src/
│   │   ├── connect4.java
│   │   └── Connect4Evaluation.java
│   └── README.md
│
├── ai.SlidingPuzzle/      # Sliding Puzzle solver with A* Search
│   ├── src/
│   │   ├── SliddingPuzzle.java
│   │   ├── Main.java
│   │   ├── ManhattanDistanceHeuristic.java
│   │   └── MisplacedTilesHeuristic.java
│   └── README.md
│
├── ai.sudoku/             # Sudoku solver with Best-First Search
│   ├── src/
│   │   ├── Sudoku.java
│   │   └── NaiveHeuristic.java
│   └── README.md
│
├── perceptron/            # Rosenblatt Perceptron (Python/NumPy)
│   ├── perceptron.py
│   ├── perceptron_main.py
│   ├── breast_cancer_experiment.py
│   ├── breast_cancer_data/
│   │   ├── wdbc.data
│   │   └── wdbc.names
│   └── README.md
│
├── bayes/                 # Naive Bayes Classifier (Python/NumPy)
│   ├── bayes.py
│   ├── bayes_main.py
│   ├── wine.data
│   ├── homework/
│   │   ├── bayes.py
│   │   ├── bayes_main.py
│   │   └── spambase.data
│   └── README.md
│
└── lectures/              # Weekly lecture slides (PDF)
    ├── week1.pdf
    ├── week2.pdf
    ├── week3.pdf
    ├── week4.pdf
    ├── week5.pdf
    ├── week6.pdf
    └── week7.pdf
```

---

## 🎮 ai.connect4 — Connect Four AI

**Language:** Java &nbsp;|&nbsp; **Algorithm:** Alpha-Beta Pruning (SAC library)

AI plays Connect Four using depth-limited minimax with Alpha-Beta pruning. The evaluation function scores board windows, center control, and piece height.

| File | Description |
|------|-------------|
| `src/connect4.java` | Game state, move generation, win/draw detection, `main()` |
| `src/Connect4Evaluation.java` | Board heuristic for Alpha-Beta search |

---

## 🧩 ai.SlidingPuzzle — Sliding Puzzle Solver

**Language:** Java &nbsp;|&nbsp; **Algorithm:** A* Search (SAC library)

Solves n×n sliding puzzles and compares two admissible heuristics across batch experiments.

| File | Description |
|------|-------------|
| `src/SliddingPuzzle.java` | Board state, shuffle, child generation |
| `src/Main.java` | Experiment runner — 3×3 (100 puzzles) and 4×4 (10 puzzles) |
| `src/ManhattanDistanceHeuristic.java` | Sum of L1 distances to goal positions |
| `src/MisplacedTilesHeuristic.java` | Count of tiles not in goal position |

---

## 🔢 ai.sudoku — Sudoku Solver

**Language:** Java &nbsp;|&nbsp; **Algorithm:** Best-First Search (SAC library)

Solves 9×9 Sudoku puzzles via constraint-based child generation and a naive heuristic.

| File | Description |
|------|-------------|
| `src/Sudoku.java` | Board, legality checks, BFS solver, `main()` |
| `src/NaiveHeuristic.java` | Heuristic: count of unfilled cells |

---

## 🧠 perceptron — Simple Perceptron Classifier

**Language:** Python 3 &nbsp;|&nbsp; **Libraries:** NumPy, scikit-learn, Matplotlib

From-scratch Rosenblatt Perceptron with synthetic demo and real-world breast cancer experiment.

| File | Description |
|------|-------------|
| `perceptron.py` | `SimplePerceptron` — fit, predict, decision_function |
| `perceptron_main.py` | Synthetic data demo with decision boundary plot |
| `breast_cancer_experiment.py` | Accuracy vs. `k_max` on UCI WDBC dataset |
| `breast_cancer_data/wdbc.data` | UCI Breast Cancer Wisconsin dataset |

```bash
cd perceptron
pip install numpy scikit-learn matplotlib
python perceptron_main.py
python breast_cancer_experiment.py
```

---

## 📊 bayes — Discrete Naive Bayes Classifier

**Language:** Python 3 &nbsp;|&nbsp; **Libraries:** NumPy

Discrete NBC with Laplace smoothing, tested on Wine and Spambase datasets.

| File | Description |
|------|-------------|
| `bayes.py` | `NBCDiscrete` — fit, predict, predict_proba |
| `bayes_main.py` | Wine dataset demo with discretization |
| `wine.data` | UCI Wine recognition dataset |
| `homework/bayes.py` | Homework variant |
| `homework/bayes_main.py` | Spambase spam detection demo |
| `homework/spambase.data` | UCI Spambase dataset |

```bash
cd bayes
pip install numpy
python bayes_main.py
```

---

## 📚 Dependencies

### Java Projects
All Java projects use the **SAC (State-and-Action Framework)** library.  
All required JARs are included in the [`lib/`](lib/) folder.

```bash
# Compile any Java project
javac -cp "lib/*" src/*.java
```

See [`lib/README.md`](lib/README.md) for details on each JAR and Eclipse setup instructions.

### Python Projects
```
numpy
scikit-learn   (perceptron only)
matplotlib     (perceptron_main.py only)
```
