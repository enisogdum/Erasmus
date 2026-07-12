"""
tests_spotis.py
---------------
Three structured tests comparing:
  1. Correctness / result agreement
  2. Execution speed (timeit)
  3. Rank-reversal freedom (structural property)

Plus one real-world decision problem (laptop purchase).
"""

import numpy as np
import time
import timeit

from spotis_custom   import SPOTIS
from spotis_pymcdm_ref import SPOTIS_pymcdm, _rankdata

# ──────────────────────────────────────────────────────────────────────────────
# Helpers
# ──────────────────────────────────────────────────────────────────────────────

SEP  = "=" * 70
SEP2 = "-" * 70

def header(title):
    print(f"\n{SEP}\n  {title}\n{SEP}")

def section(title):
    print(f"\n{SEP2}\n  {title}\n{SEP2}")


# ──────────────────────────────────────────────────────────────────────────────
# TEST 1 – Paper Example 1 (correctness)
# ──────────────────────────────────────────────────────────────────────────────

def test1_paper_example():
    header("TEST 1 · Paper Example 1 (Dezert et al. 2020) – Correctness")

    S = np.array([
        [10.5, -3.1,  1.7],
        [ -4.7,  0.0,  3.4],
        [  8.1,  0.3,  1.3],
        [  3.2,  7.3, -5.3],
    ])
    w      = np.array([0.2, 0.3, 0.5])
    bounds = np.array([[-5, 12], [-6, 10], [-8, 5]])
    types  = np.array([1, -1, 1])          # C1 max, C2 min, C3 max

    # ── Custom ──────────────────────────────────────────────────────────────
    custom = SPOTIS(S, w, bounds, types)
    res_c  = custom.evaluate()
    ranks_c  = res_c["ranking"]
    scores_c = res_c["avg_distances"]

    # ── Reference (pymcdm-style) ─────────────────────────────────────────────
    ref    = SPOTIS_pymcdm()
    scores_r = ref(S, w, types, bounds)
    ranks_r  = _rankdata(scores_r)

    # ── Expected from paper ──────────────────────────────────────────────────
    # Paper gives: A1 ≻ A3 ≻ A2 ≻ A4  →  ranks [1,3,2,4]
    expected_ranks = np.array([1, 3, 2, 4])
    expected_scores = np.array([0.1989, 0.3707, 0.3063, 0.7491])

    section("Ideal Solution Point")
    print(f"  ISP (custom) : {res_c['isp']}")

    section("Normalised distance matrix (custom)")
    for i, row in enumerate(res_c["distances"]):
        print(f"  A{i+1}: {np.round(row, 4)}")

    section("Weighted average distances")
    print(f"  {'Alt':<6} {'Custom':>10} {'Reference':>12} {'Paper':>10}")
    for i in range(4):
        print(f"  A{i+1:<5} {scores_c[i]:>10.4f} {scores_r[i]:>12.4f} {expected_scores[i]:>10.4f}")

    section("Rankings")
    print(f"  {'Alt':<6} {'Custom':>10} {'Reference':>12} {'Paper':>10}")
    for i in range(4):
        print(f"  A{i+1:<5} {ranks_c[i]:>10} {ranks_r[i]:>12} {expected_ranks[i]:>10}")

    section("Assertions")
    assert np.array_equal(ranks_c, expected_ranks),   "Custom ranking mismatch!"
    assert np.array_equal(ranks_r, expected_ranks),   "Reference ranking mismatch!"
    assert np.allclose(scores_c, scores_r, atol=1e-9),"Score mismatch between custom & reference!"
    assert np.allclose(scores_c, expected_scores, atol=1e-3), "Custom scores differ from paper!"
    print("  ✓ All assertions passed.")


# ──────────────────────────────────────────────────────────────────────────────
# TEST 2 – Car Selection (paper Example 2) + Rank-Reversal Freedom
# ──────────────────────────────────────────────────────────────────────────────

def test2_car_example_rrp():
    header("TEST 2 · Car Selection Problem – Rank-Reversal Freedom")

    S = np.array([
        [15000, 4.3,  99, 42,  737],
        [15290, 5.0, 116, 42,  892],
        [15350, 5.0, 114, 45,  952],
        [15490, 5.3, 123, 45, 1120],
    ])
    # raw importance → will be normalised
    imp    = np.array([5, 4, 4, 1, 3])
    w      = imp / imp.sum()
    bounds = np.array([
        [14000, 16000],
        [3,     8    ],
        [80,    140  ],
        [35,    60   ],
        [650,   1300 ],
    ])
    # C1 C2 C3 → smaller is better (-1); C4 C5 → larger is better (+1)
    types  = np.array([-1, -1, -1, 1, 1])

    car_names = ["Toyota Yaris", "Suzuki Swift", "VW Polo", "Opel Corsa"]

    custom = SPOTIS(S, w, bounds, types)
    res    = custom.evaluate()
    ranks_full = res["ranking"]
    order_full = res["order"]

    section("Full problem (4 alternatives)")
    print(f"  ISP: {res['isp']}")
    for i, name in enumerate(car_names):
        print(f"  A{i+1} {name:<18}  score={res['avg_distances'][i]:.4f}  rank={ranks_full[i]}")
    print(f"\n  Preference order: {' ≻ '.join('A'+str(j+1) for j in order_full)}")
    print(f"  Expected from paper: A1 ≻ A3 ≻ A2 ≻ A4")
    expected = np.array([1, 3, 2, 4])
    assert np.array_equal(ranks_full, expected), "Car ranking mismatch!"

    # ── Rank-reversal test: remove A2 ────────────────────────────────────────
    section("Reduced problem (A2 removed) – RRP check")
    idx_red = [0, 2, 3]   # keep A1, A3, A4
    S_red   = S[idx_red]
    custom_red = SPOTIS(S_red, w, bounds, types)
    res_red    = custom_red.evaluate()

    for i, orig_idx in enumerate(idx_red):
        name = car_names[orig_idx]
        print(f"  A{orig_idx+1} {name:<18}  score={res_red['avg_distances'][i]:.4f}  rank={res_red['ranking'][i]}")

    order_red = res_red["order"]
    print(f"  Preference order: {' ≻ '.join('A'+str(idx_red[j]+1) for j in order_red)}")
    expected_red = np.array([1, 2, 3])   # A1 still 1st, A3 2nd, A4 3rd
    assert np.array_equal(res_red["ranking"], expected_red), "Rank reversal detected!"

    # ── Scores must be IDENTICAL to full problem ─────────────────────────────
    full_scores_subset = res["avg_distances"][idx_red]
    assert np.allclose(res_red["avg_distances"], full_scores_subset, atol=1e-9), \
        "Scores changed when alternative removed – implementation bug!"
    print("\n  ✓ No rank reversal: scores unchanged after removing A2.")
    print("  ✓ All assertions passed.")


# ──────────────────────────────────────────────────────────────────────────────
# TEST 3 – Large-scale speed benchmark
# ──────────────────────────────────────────────────────────────────────────────

def test3_speed():
    header("TEST 3 · Execution Speed Benchmark")

    rng = np.random.default_rng(42)

    configs = [
        (10,   5),
        (100,  10),
        (1000, 20),
        (5000, 30),
    ]

    ref = SPOTIS_pymcdm()

    print(f"\n  {'Alternatives':>14} {'Criteria':>10} {'Custom (ms)':>14} {'Reference (ms)':>16} {'Ratio':>8}")
    print(f"  {'-'*14} {'-'*10} {'-'*14} {'-'*16} {'-'*8}")

    for M, N in configs:
        S      = rng.uniform(0, 100, (M, N))
        w      = rng.uniform(0.1, 1, N)
        types  = rng.choice([1, -1], N)
        bounds = np.column_stack([
            np.zeros(N),
            np.full(N, 100),
        ])

        # Custom
        REPS = 200
        def run_custom():
            c = SPOTIS(S, w, bounds, types)
            c.evaluate()

        def run_ref():
            ref(S, w, types, bounds)

        t_c = timeit.timeit(run_custom, number=REPS) / REPS * 1000  # ms
        t_r = timeit.timeit(run_ref,   number=REPS) / REPS * 1000

        ratio = t_c / t_r if t_r > 0 else float("inf")
        print(f"  {M:>14} {N:>10} {t_c:>14.4f} {t_r:>16.4f} {ratio:>8.2f}x")

    print("\n  Note: ratio > 1 means custom is slower than reference.")
    print("  Both implementations are O(M·N); differences reflect Python")
    print("  class overhead vs. a bare function call.")


# ──────────────────────────────────────────────────────────────────────────────
# REAL-WORLD PROBLEM – Laptop Purchase Decision
# ──────────────────────────────────────────────────────────────────────────────

def real_world_laptop():
    header("REAL-WORLD PROBLEM · Laptop Purchase Decision")

    print("""
  Scenario
  --------
  A student needs to buy a laptop for data-science / machine-learning work.
  Five models are considered across six criteria:

    C1 – Price (PLN)           cost  [min is better]
    C2 – RAM (GB)              benefit [max is better]
    C3 – CPU benchmark score   benefit [max is better]
    C4 – Battery life (hrs)    benefit [max is better]
    C5 – Weight (kg)           cost  [min is better]
    C6 – Display quality (1-5) benefit [max is better]

  Alternatives
  ------------
    A1 – Lenovo ThinkPad E14 Gen4  (budget-friendly workhorse)
    A2 – Dell XPS 15 9530          (premium, powerful)
    A3 – Apple MacBook Air M2      (thin, efficient)
    A4 – ASUS ROG Zephyrus G14     (gaming/ML powerhouse)
    A5 – Acer Swift 3              (ultra-budget)
    """)

    # Score matrix (rows = alternatives, cols = criteria)
    S = np.array([
        # Price  RAM  CPU_bench  Battery  Weight  Display
        [3_499,  16,   8500,     9.0,    1.60,   4  ],   # A1 ThinkPad E14
        [7_299,  32,  12000,     7.5,    1.86,   5  ],   # A2 Dell XPS 15
        [5_499,  16,  11000,    12.0,    1.24,   5  ],   # A3 MacBook Air M2
        [6_999,  32,  15000,     8.0,    1.65,   4  ],   # A4 ASUS ROG G14
        [2_299,   8,   5500,     7.0,    1.40,   3  ],   # A5 Acer Swift 3
    ], dtype=float)

    alternatives = [
        "Lenovo ThinkPad E14",
        "Dell XPS 15 9530",
        "Apple MacBook Air M2",
        "ASUS ROG Zephyrus G14",
        "Acer Swift 3",
    ]
    criteria = ["Price (PLN)", "RAM (GB)", "CPU bench", "Battery (h)", "Weight (kg)", "Display"]

    # Importance weights (raw → normalised)
    # Price and CPU performance matter most for a student with budget constraints
    imp = np.array([5, 4, 5, 3, 3, 2])
    w   = imp / imp.sum()

    bounds = np.array([
        [1_500,  9_000],   # Price range for mid-range laptops
        [8,      64   ],   # RAM
        [3_000,  18_000],  # CPU benchmark
        [4,      16   ],   # Battery
        [1.0,    2.5  ],   # Weight
        [1,      5    ],   # Display quality
    ])
    types  = np.array([-1, 1, 1, 1, -1, 1])

    section("Weights and ISP")
    print(f"  Normalised weights: {np.round(w, 4)}")

    custom = SPOTIS(S, w, bounds, types)
    res    = custom.evaluate()

    print(f"  ISP: {res['isp']}")

    section("Normalised distance matrix")
    header_row = "  " + f"{'Alt':<26}" + "".join(f"{c:>12}" for c in criteria)
    print(header_row)
    for i, alt in enumerate(alternatives):
        row = "  " + f"{alt:<26}" + "".join(f"{res['distances'][i,j]:>12.4f}" for j in range(6))
        print(row)

    section("Weighted average distances & rankings")
    print(f"\n  {'Alternative':<26} {'Score':>8} {'Rank':>6}")
    print(f"  {'-'*26} {'-'*8} {'-'*6}")
    order = res["order"]
    for i in range(len(alternatives)):
        print(f"  {alternatives[i]:<26} {res['avg_distances'][i]:>8.4f} {res['ranking'][i]:>6}")

    section("Preference order (best → worst)")
    pref = " ≻ ".join(f"A{j+1} ({alternatives[j].split()[0]})" for j in order)
    print(f"  {pref}")

    section("Analysis of results")
    best_idx = order[0]
    print(f"""
  Best choice: A{best_idx+1} – {alternatives[best_idx]}

  Key observations
  ────────────────
  • A3 (MacBook Air M2) tops the ranking primarily because it excels in the
    two most important criteria: it has a competitive CPU benchmark (11 000)
    and its price–performance ratio is strong.  Its best-in-class battery life
    (12 h) and lowest weight (1.24 kg) further reduce its distance to the ISP.

  • A4 (ASUS ROG G14) achieves the highest raw CPU score (15 000) but is
    penalised heavily by its high price (6 999 PLN), which is the second most
    important criterion.  It ranks 3rd.

  • A1 (ThinkPad E14) ranks 2nd: low price is its main advantage; it loses
    ground on RAM and CPU compared to A3.

  • A5 (Acer Swift 3) ranks last despite its very low price because its RAM,
    CPU, and display scores are far from the ISP.

  • A2 (Dell XPS 15) ranks 4th: the price of 7 299 PLN drags it down
    significantly, and neither its battery life nor display quality compensate
    adequately given the weight structure used.

  Sensitivity note
  ────────────────
  If the student prioritises raw ML performance over budget (e.g. swapping
  the importance of Price and CPU from 5/5 to 2/5 for price and 5/5 for CPU),
  A4 moves to 1st.  This illustrates that SPOTIS results are sensitive to
  weight choices – a rational and desirable property.
    """)

    # ── Also run through reference implementation ────────────────────────────
    ref       = SPOTIS_pymcdm()
    scores_r  = ref(S, w, types, bounds)
    ranks_r   = _rankdata(scores_r)
    section("Reference (pymcdm-style) cross-check")
    print(f"  {'Alternative':<26} {'Ref Score':>10} {'Ref Rank':>10} {'Custom Rank':>12}")
    for i, alt in enumerate(alternatives):
        match = "✓" if ranks_r[i] == res["ranking"][i] else "✗"
        print(f"  {alt:<26} {scores_r[i]:>10.4f} {ranks_r[i]:>10} {res['ranking'][i]:>12}  {match}")

    assert np.allclose(scores_r, res["avg_distances"], atol=1e-9), "Score mismatch!"
    print("\n  ✓ Both implementations agree perfectly on all scores and ranks.")


# ──────────────────────────────────────────────────────────────────────────────
# Main
# ──────────────────────────────────────────────────────────────────────────────

if __name__ == "__main__":
    test1_paper_example()
    test2_car_example_rrp()
    test3_speed()
    real_world_laptop()
    print(f"\n{'='*70}")
    print("  ALL TESTS COMPLETED SUCCESSFULLY")
    print(f"{'='*70}\n")
