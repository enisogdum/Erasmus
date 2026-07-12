# Naive Bayes Classifier (Discrete)

A from-scratch implementation of a **Discrete Naive Bayes Classifier** (`NBCDiscrete`) using NumPy, with optional **Laplace smoothing**. Compatible with the scikit-learn interface (or uses a built-in fallback).

---

## Files

| File | Description |
|------|-------------|
| `bayes.py` | `NBCDiscrete` class — core implementation |
| `bayes_main.py` | Demo: Wine dataset classification with discretization |
| `wine.data` | UCI Wine recognition dataset (178 samples, 13 features, 3 classes) |
| `homework/bayes.py` | Homework variant of the classifier |
| `homework/bayes_main.py` | Homework variant trained on the Spambase dataset |
| `homework/spambase.data` | UCI Spambase dataset (~4600 emails, 57 features) |

---

## `NBCDiscrete` API

```python
from bayes import NBCDiscrete
import numpy as np

domain_sizes = 5 * np.ones(n_features, dtype=np.int8)  # 5 bins per feature
clf = NBCDiscrete(domain_sizes, laplace=True)
clf.fit(X_train_discrete, y_train)
y_pred = clf.predict(X_test_discrete)
proba  = clf.predict_proba(X_test_discrete)
```

| Parameter | Default | Description |
|-----------|---------|-------------|
| `domain_sizes` | required | Array of size `n_features`; number of discrete values per feature |
| `laplace` | `False` | Enable Laplace (add-one) smoothing to avoid zero probabilities |

> ⚠️ Input `X` must be **discretized** (integer-valued) before passing to `fit`/`predict`. See `bayes_main.py` for a `discretize()` helper.

---

## Wine Dataset Demo (`bayes_main.py`)

- 75/25 random train-test split
- Features discretized into 5 bins
- Laplace smoothing enabled
- Reports train accuracy and test accuracy

---

## Requirements

```bash
pip install numpy
```

## How to Run

```bash
cd bayes

# Wine dataset demo
python bayes_main.py

# Homework: Spambase demo
cd homework
python bayes_main.py
```
