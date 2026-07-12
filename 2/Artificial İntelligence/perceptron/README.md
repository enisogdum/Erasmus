# Perceptron Classifier

A from-scratch implementation of the **Rosenblatt Perceptron** using NumPy, compatible with the scikit-learn `BaseEstimator`/`ClassifierMixin` interface.

---

## Files

| File | Description |
|------|-------------|
| `perceptron.py` | `SimplePerceptron` class — core implementation |
| `perceptron_main.py` | Demo: synthetic linearly-separable data + decision boundary plot |
| `breast_cancer_experiment.py` | Experiment: accuracy vs. `k_max` on UCI Breast Cancer dataset |
| `breast+cancer+wisconsin+diagnostic/wdbc.data` | UCI WDBC dataset (569 samples, 30 features) |

---

## `SimplePerceptron` API

```python
from perceptron import SimplePerceptron

clf = SimplePerceptron(learning_rate=0.1, k_max=None)
clf.fit(X_train, y_train)   # Train until convergence (or k_max steps)
y_pred = clf.predict(X_test)
scores = clf.decision_function(X_test)
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `learning_rate` | `0.1` | Step size for weight updates |
| `k_max` | `None` | Max training steps; `None` = train until convergence |

After fitting:
- `clf.w_` — learned weight vector (including bias at index 0)
- `clf.k_` — number of training steps taken

---

## Experiments (`breast_cancer_experiment.py`)

Tests the perceptron on the **Breast Cancer Wisconsin (Diagnostic)** dataset:

- 569 samples, 30 features, binary labels (M/B)
- Features are standardized with `StandardScaler`
- For each `k_max` ∈ {1000, 2000, 10000}: 10 random 75/25 train-test splits, reporting mean ± std accuracy

---

## Requirements

```bash
pip install numpy scikit-learn matplotlib
```

## How to Run

```bash
cd perceptron

# Demo with synthetic data and plot
python perceptron_main.py

# Breast cancer experiment
python breast_cancer_experiment.py
```
