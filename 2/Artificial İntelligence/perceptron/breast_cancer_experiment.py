"""
Breast Cancer Wisconsin (Diagnostic) – SimplePerceptron experiments.

Dataset: wdbc.data (UCI)
  Column 0  : sample ID  (ignored)
  Column 1  : label      (M = Malignant, B = Benign)
  Columns 2-31: 30 real-valued features

Experiment:
  For k_max in [1 000, 2 000, 10 000]:
    Repeat 10 times:
      - Random 75/25 train-test split
      - Fit SimplePerceptron on training set
      - Predict on test set
      - Compute accuracy
    Report mean ± std accuracy over 10 runs
"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from perceptron import SimplePerceptron

# ── 1. Load data ──────────────────────────────────────────────────────────────
DATA_PATH = "breast_cancer_data/wdbc.data"

data = np.genfromtxt(DATA_PATH, delimiter=",", dtype=str)

# Column 1 → labels (M / B), columns 2-31 → features
y = data[:, 1]                    # string array: 'M' or 'B'
X = data[:, 2:].astype(float)    # shape (569, 30)

print(f"Dataset loaded: {X.shape[0]} samples, {X.shape[1]} features")
print(f"Classes: {np.unique(y, return_counts=True)}\n")

# ── 2. Experiment settings ────────────────────────────────────────────────────
K_MAX_VALUES = [1_000, 2_000, 10_000]
N_EXPERIMENTS = 10
TEST_SIZE = 0.25
LEARNING_RATE = 0.1

# ── 3. Run experiments ────────────────────────────────────────────────────────
print(f"{'k_max':>8}  {'Mean Accuracy':>14}  {'Std':>8}  {'Individual accuracies'}")
print("-" * 75)

for k_max in K_MAX_VALUES:
    accuracies = []

    for seed in range(N_EXPERIMENTS):
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=TEST_SIZE, random_state=seed
        )

        # Feature scaling helps the perceptron converge faster / more reliably
        scaler = StandardScaler()
        X_train_s = scaler.fit_transform(X_train)
        X_test_s  = scaler.transform(X_test)
        #X_train_s = X_train
        #X_test_s = X_test

        clf = SimplePerceptron(learning_rate=LEARNING_RATE, k_max=k_max)
        clf.fit(X_train_s, y_train)

        y_pred = clf.predict(X_test_s)
        acc = np.mean(y_pred == y_test)
        accuracies.append(acc)

    mean_acc = np.mean(accuracies)
    std_acc  = np.std(accuracies)
    indiv    = ", ".join(f"{a:.4f}" for a in accuracies)

    print(f"{k_max:>8,}  {mean_acc:>13.4f}%  {std_acc:>7.4f}  [{indiv}]")

print("\nDone.")
