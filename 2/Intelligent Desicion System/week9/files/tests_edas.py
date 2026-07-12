"""
tests_edas.py
=============
Three structured tests comparing:
  (A) Custom EDAS implementation  (edas_custom.py)
  (B) PyMCDM reference EDAS       (edas_pymcdm_ref.py)

Test 1 — Paper reproduction
    Uses the exact 47-SKU inventory dataset from Table 1 of the original paper.
    Expected AS values and ABC classes are taken from Table 2 / Table 3.

Test 2 — MCDM comparative example
    Uses the 10-alternative, 7-criterion dataset from Table 5 of the paper.
    Expected ranking (Set 1 weights) is taken from Table 7.

Test 3 — Large random matrix / execution-speed benchmark
    Generates 500 random alternatives × 20 criteria and measures wall-clock
    time for 1 000 repeated calls of each implementation.
"""

import time
import sys
import numpy as np
import pandas as pd

sys.path.insert(0, "/home/claude")
from edas_custom import EDAS
from edas_pymcdm_ref import _pymcdm_edas

# ─────────────────────────────────────────────────────────────────────────────
# Helpers
# ─────────────────────────────────────────────────────────────────────────────

def spearman(a: np.ndarray, b: np.ndarray) -> float:
    n = len(a)
    d2 = np.sum((a - b) ** 2)
    return 1 - 6 * d2 / (n * (n ** 2 - 1))


def mae(a: np.ndarray, b: np.ndarray) -> float:
    return float(np.abs(a - b).mean())


def max_ae(a: np.ndarray, b: np.ndarray) -> float:
    return float(np.abs(a - b).max())


# ─────────────────────────────────────────────────────────────────────────────
# Test 1 – Paper data (47 SKUs)
# ─────────────────────────────────────────────────────────────────────────────

def test1_paper_data():
    print("\n" + "=" * 70)
    print("TEST 1 – Paper reproduction: 47-SKU inventory dataset")
    print("=" * 70)

    # Table 1 data: [avg_unit_cost, annual_dollar_usage, lead_time]
    data_raw = [
        [49.92,  5840.64, 2], [210,    5670,    5], [23.76,  5037.12, 4],
        [27.73,  4769.56, 1], [57.98,  3478.8,  3], [31.24,  2936.67, 3],
        [28.2,   2820,    3], [55,     2640,    4], [73.44,  2423.52, 6],
        [160.5,  2407.5,  4], [5.12,   1075.2,  2], [20.87,  1043.5,  5],
        [86.5,   1038,    7], [110.4,  883.2,   5], [71.2,   854.4,   3],
        [45,     810,     3], [14.66,  703.68,  4], [49.5,   594,     6],
        [47.5,   570,     5], [58.45,  467.6,   4], [24.4,   463.6,   4],
        [65,     455,     4], [86.5,   432.5,   4], [33.2,   398.4,   3],
        [37.05,  370.5,   1], [33.84,  338.4,   3], [84.03,  336.12,  1],
        [78.4,   313.6,   6], [134.34, 268.68,  7], [56,     224,     1],
        [72,     216,     5], [53.02,  212.08,  2], [49.48,  197.92,  5],
        [7.07,   190.89,  7], [60.6,   181.8,   3], [40.82,  163.28,  3],
        [30,     150,     5], [67.4,   134.8,   3], [59.6,   119.2,   5],
        [51.68,  103.36,  6], [19.8,   79.2,    2], [37.7,   75.4,    2],
        [29.89,  59.78,   5], [48.3,   48.3,    3], [34.4,   34.4,    7],
        [28.8,   28.8,    3], [8.46,   25.38,   5],
    ]

    matrix = np.array(data_raw, dtype=float)
    weights = np.array([1/3, 1/3, 1/3])
    types   = np.array([1, 1, 1])          # all beneficial

    # Expected AS from Table 2 (paper), in item order S1…S47
    expected_AS = np.array([
        0.66, 1.00, 0.61, 0.43, 0.60, 0.45, 0.43, 0.60, 0.64, 0.72,
        0.16, 0.36, 0.58, 0.54, 0.41, 0.34, 0.24, 0.40, 0.37, 0.37,
        0.23, 0.37, 0.39, 0.19, 0.08, 0.18, 0.19, 0.39, 0.47, 0.13,
        0.35, 0.18, 0.30, 0.14, 0.25, 0.18, 0.20, 0.25, 0.31, 0.30,
        0.00, 0.08, 0.18, 0.18, 0.23, 0.09, 0.08,
    ])

    edas_custom = EDAS(verbose=True)
    AS_custom   = edas_custom(matrix, weights, types)
    AS_pymcdm   = _pymcdm_edas(matrix, weights, types)

    rank_custom = EDAS.rank(AS_custom)
    rank_pymcdm = EDAS.rank(AS_pymcdm)
    rank_paper  = EDAS.rank(expected_AS)

    # ABC classification: top-10 = A, next-14 = B, rest = C
    def abc(ranks, n_A=10, n_B=14):
        classes = np.empty(len(ranks), dtype=str)
        classes[ranks <= n_A] = "A"
        classes[(ranks > n_A) & (ranks <= n_A + n_B)] = "B"
        classes[ranks > n_A + n_B] = "C"
        return classes

    cls_custom = abc(rank_custom)
    cls_pymcdm = abc(rank_pymcdm)
    cls_paper  = abc(rank_paper)

    # Expected paper classes (Table 3, Proposed method column, same item order)
    expected_cls = np.array([
        'A','A','A','B','A','B','B','A','A','A',
        'C','B','A','A','B','B','C','B','B','B',
        'C','B','B','C','C','C','C','B','A','C',
        'B','C','C','C','C','C','C','C','B','C',
        'C','C','C','C','C','C','C',
    ])

    agreement_custom = np.mean(cls_custom == expected_cls)
    agreement_pymcdm = np.mean(cls_pymcdm == expected_cls)
    agreement_cross  = np.mean(cls_custom == cls_pymcdm)

    print(f"\n{'Item':<6} {'AS_paper':>10} {'AS_custom':>10} {'AS_pymcdm':>10} "
          f"{'Cls_paper':>10} {'Cls_custom':>10} {'Cls_pymcdm':>10}")
    print("-" * 70)
    for i in range(47):
        print(f"S{i+1:<5} {expected_AS[i]:>10.2f} {AS_custom[i]:>10.2f} "
              f"{AS_pymcdm[i]:>10.2f} {expected_cls[i]:>10} "
              f"{cls_custom[i]:>10} {cls_pymcdm[i]:>10}")

    print(f"\n── Numerical accuracy ───────────────────────────────────")
    print(f"  Custom  vs paper:  MAE={mae(AS_custom, expected_AS):.4f}  "
          f"MaxAE={max_ae(AS_custom, expected_AS):.4f}  "
          f"Spearman(rank)={spearman(rank_custom, rank_paper):.4f}")
    print(f"  PyMCDM  vs paper:  MAE={mae(AS_pymcdm, expected_AS):.4f}  "
          f"MaxAE={max_ae(AS_pymcdm, expected_AS):.4f}  "
          f"Spearman(rank)={spearman(rank_pymcdm, rank_paper):.4f}")
    print(f"  Custom  vs pymcdm: MAE={mae(AS_custom, AS_pymcdm):.6f}  "
          f"MaxAE={max_ae(AS_custom, AS_pymcdm):.6f}  "
          f"Spearman(rank)={spearman(rank_custom, rank_pymcdm):.6f}")

    print(f"\n── ABC classification agreement ─────────────────────────")
    print(f"  Custom  vs paper:  {agreement_custom*100:.1f}%")
    print(f"  PyMCDM  vs paper:  {agreement_pymcdm*100:.1f}%")
    print(f"  Custom  vs PyMCDM: {agreement_cross*100:.1f}%")

    return {
        "AS_custom": AS_custom,
        "AS_pymcdm": AS_pymcdm,
        "rank_custom": rank_custom,
        "rank_pymcdm": rank_pymcdm,
        "cls_custom": cls_custom,
        "cls_pymcdm": cls_pymcdm,
    }


# ─────────────────────────────────────────────────────────────────────────────
# Test 2 – MCDM comparative example (Table 5, Set-1 weights)
# ─────────────────────────────────────────────────────────────────────────────

def test2_mcdm_example():
    print("\n" + "=" * 70)
    print("TEST 2 – MCDM comparative example (Table 5, Set-1 weights)")
    print("=" * 70)

    matrix = np.array([
        [23, 264, 2.37, 0.05, 167,  8900, 8.71],
        [20, 220, 2.20, 0.04, 171,  9100, 8.23],
        [17, 231, 1.98, 0.15, 192, 10800, 9.91],
        [12, 210, 1.73, 0.20, 195, 12300,10.21],
        [15, 243, 2.00, 0.14, 187, 12600, 9.34],
        [14, 222, 1.89, 0.13, 180, 13200, 9.22],
        [21, 262, 2.43, 0.06, 160, 10300, 8.93],
        [20, 256, 2.60, 0.07, 163, 11400, 8.44],
        [19, 266, 2.10, 0.06, 157, 11200, 9.04],
        [ 8, 218, 1.94, 0.11, 190, 13400,10.11],
    ], dtype=float)

    # C1, C2, C3 beneficial; C4, C5, C6, C7 non-beneficial
    types   = np.array([1, 1, 1, -1, -1, -1, -1])

    # Seven weight sets from Table 6
    weight_sets = {
        "Set 1": [0.250, 0.214, 0.179, 0.143, 0.107, 0.071, 0.036],
        "Set 2": [0.182, 0.212, 0.182, 0.152, 0.121, 0.091, 0.061],
        "Set 3": [0.139, 0.167, 0.194, 0.167, 0.139, 0.111, 0.083],
        "Set 4": [0.108, 0.135, 0.162, 0.189, 0.162, 0.135, 0.108],
        "Set 5": [0.083, 0.111, 0.139, 0.167, 0.194, 0.167, 0.139],
        "Set 6": [0.061, 0.091, 0.121, 0.152, 0.182, 0.212, 0.182],
        "Set 7": [0.036, 0.071, 0.107, 0.143, 0.179, 0.214, 0.250],
    }

    # Expected ranks from Table 7 (EDAS rows)
    expected_ranks = {
        "Set 1": [1, 4, 6, 10, 7, 8, 2, 3, 5, 9],
        "Set 2": [1, 4, 6, 10, 7, 8, 2, 3, 5, 9],
        "Set 3": [1, 3, 7, 10, 6, 8, 2, 4, 5, 9],
        "Set 4": [1, 2, 7, 10, 6, 8, 3, 4, 5, 9],
        "Set 5": [1, 2, 6, 10, 7, 8, 3, 4, 5, 9],
        "Set 6": [1, 2, 6, 10, 7, 8, 3, 4, 5, 9],
        "Set 7": [1, 2, 6, 10, 7, 8, 3, 4, 5, 9],
    }

    edas_custom = EDAS()
    print(f"\n{'Set':<8} {'Custom ranks (A1..A10)':<35} {'PyMCDM ranks':<35} "
          f"{'Spm(C,exp)':>11} {'Spm(P,exp)':>11} {'Spm(C,P)':>9}")
    print("-" * 110)

    for name, w in weight_sets.items():
        weights = np.array(w)
        AS_c = edas_custom(matrix, weights, types)
        AS_p = _pymcdm_edas(matrix, weights, types)
        r_c  = EDAS.rank(AS_c)
        r_p  = EDAS.rank(AS_p)
        r_e  = np.array(expected_ranks[name])
        spm_ce = spearman(r_c, r_e)
        spm_pe = spearman(r_p, r_e)
        spm_cp = spearman(r_c, r_p)
        print(f"{name:<8} {str(list(r_c)):<35} {str(list(r_p)):<35} "
              f"{spm_ce:>11.4f} {spm_pe:>11.4f} {spm_cp:>9.4f}")

    print("\nLegend: Spm = Spearman rank correlation (1.0 = perfect agreement)")


# ─────────────────────────────────────────────────────────────────────────────
# Test 3 – Execution speed benchmark
# ─────────────────────────────────────────────────────────────────────────────

def test3_speed_benchmark():
    print("\n" + "=" * 70)
    print("TEST 3 – Execution speed benchmark (500 alternatives × 20 criteria)")
    print("=" * 70)

    rng = np.random.default_rng(42)

    sizes = [
        (50,   5,   1_000),
        (500,  20,  500),
        (5000, 50,  100),
    ]

    edas_custom = EDAS()

    for n, m, reps in sizes:
        matrix  = rng.uniform(1, 100, size=(n, m))
        weights = rng.uniform(0, 1,   size=(m,))
        types   = rng.choice([-1, 1], size=(m,))

        # ── Custom ────────────────────────────────────────────────────
        t0 = time.perf_counter()
        for _ in range(reps):
            AS_c = edas_custom(matrix, weights, types)
        t_custom = (time.perf_counter() - t0) / reps * 1e6   # µs per call

        # ── PyMCDM ref ────────────────────────────────────────────────
        t0 = time.perf_counter()
        for _ in range(reps):
            AS_p = _pymcdm_edas(matrix, weights, types)
        t_pymcdm = (time.perf_counter() - t0) / reps * 1e6

        diff_mae   = mae(AS_c, AS_p)
        diff_maxae = max_ae(AS_c, AS_p)
        rank_spm   = spearman(EDAS.rank(AS_c), EDAS.rank(AS_p))

        print(f"\n  Matrix {n}×{m}  ({reps} repetitions)")
        print(f"    Custom  : {t_custom:8.2f} µs/call")
        print(f"    PyMCDM  : {t_pymcdm:8.2f} µs/call")
        print(f"    Ratio   : {t_custom/t_pymcdm:.3f}×  (custom/pymcdm)")
        print(f"    Score agreement — MAE={diff_mae:.2e}  MaxAE={diff_maxae:.2e}  "
              f"Rank Spearman={rank_spm:.6f}")


# ─────────────────────────────────────────────────────────────────────────────
# Main
# ─────────────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    results = test1_paper_data()
    test2_mcdm_example()
    test3_speed_benchmark()
    print("\n" + "=" * 70)
    print("All tests completed.")
    print("=" * 70)
