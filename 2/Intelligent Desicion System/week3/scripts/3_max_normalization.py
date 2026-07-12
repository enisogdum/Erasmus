import numpy as np

# =============================================================================
# TOPSIS Implementation
# =============================================================================

def topsis(X, w, criteria_types):
    """
    TOPSIS Implementation from scratch.

    Args:
        X              : numpy array (n x m) — decision matrix
        w              : numpy array (m,)    — weights vector
        criteria_types : list of 'min'/'max' — one per criterion

    Returns:
        dict with all intermediate steps + final preference vector and ranking
    """
    n, m = X.shape

    # Step 1 — Max Normalization
    maxs = np.max(X, axis=0)
    R = X / maxs

    # Step 2 — Weighted Normalized Matrix
    V = R * w

    # Step 3 — Ideal (A+) and Anti-Ideal (A-) Solutions
    ideal      = np.zeros(m)
    anti_ideal = np.zeros(m)

    for j in range(m):
        if criteria_types[j] == 'max':
            ideal[j]      = np.max(V[:, j])
            anti_ideal[j] = np.min(V[:, j])
        elif criteria_types[j] == 'min':
            ideal[j]      = np.min(V[:, j])
            anti_ideal[j] = np.max(V[:, j])
        else:
            raise ValueError(f"Criterion type must be 'max' or 'min', got: {criteria_types[j]}")

    # Step 4 — Euclidean Distances
    d_plus  = np.sqrt(np.sum((V - ideal)      ** 2, axis=1))
    d_minus = np.sqrt(np.sum((V - anti_ideal) ** 2, axis=1))

    # Step 5 — Closeness Coefficient (Preference Vector)
    C = d_minus / (d_plus + d_minus)

    # Step 6 — Ranking (higher C = better rank)
    sorted_indices = np.argsort(C)[::-1]
    rank_vector    = np.empty(n, dtype=int)
    rank_vector[sorted_indices] = np.arange(1, n + 1)

    return {
        'normalized_matrix'  : R,
        'weighted_matrix'    : V,
        'ideal_solution'     : ideal,
        'anti_ideal_solution': anti_ideal,
        'distances_ideal'    : d_plus,
        'distances_anti_ideal': d_minus,
        'preference_vector'  : C,
        'ranking_vector'     : rank_vector,
        'sorted_indices'     : sorted_indices,
    }


# =============================================================================
# Helper — Pretty-print a matrix as a table
# =============================================================================

def print_matrix(data, row_labels, col_labels, title):
    """Print a 2-D array (or 1-D row) as a formatted table."""
    print(f"\n{'='*70}")
    print(f"  {title}")
    print(f"{'='*70}")

    col_w = 16
    row_w = 28

    # Header
    header = f"{'Alternative':<{row_w}}" + "".join(f"{c:>{col_w}}" for c in col_labels)
    print(header)
    print("-" * len(header))

    # Rows
    if np.ndim(data) == 1:
        data = data.reshape(1, -1)

    for i, label in enumerate(row_labels):
        row_str = f"{label:<{row_w}}" + "".join(f"{data[i, j]:>{col_w}.4f}" for j in range(data.shape[1]))
        print(row_str)


def print_row(values, col_labels, label, row_w=28, col_w=16):
    row_str = f"{label:<{row_w}}" + "".join(f"{v:>{col_w}.4f}" for v in values)
    print(row_str)


# =============================================================================
# Decision Problem — Choosing a Smartphone
# =============================================================================

alternatives = [
    "iPhone 15 Pro",
    "Samsung Galaxy S24 Ultra",
    "Google Pixel 8 Pro",
    "OnePlus 12",
    "Xiaomi 14 Pro",
    "Sony Xperia 1 V",
    "Asus Zenfone 10",
    "Motorola Edge 50 Pro",
    "Nothing Phone (2)",
    "Honor Magic 6 Pro",
]

criteria       = ["Price (USD)", "Battery (mAh)", "Camera Score", "Perf. Score"]
criteria_types = ['min', 'max', 'max', 'max']
w              = np.array([0.35, 0.15, 0.25, 0.25])

# Decision matrix  [Price, Battery, Camera, Performance]
X = np.array([
    [ 999, 3274, 154, 7192],   # iPhone 15 Pro
    [1299, 5000, 144, 7249],   # Samsung Galaxy S24 Ultra
    [ 999, 5050, 153, 4425],   # Google Pixel 8 Pro
    [ 799, 5400, 140, 6800],   # OnePlus 12
    [ 899, 4880, 144, 6900],   # Xiaomi 14 Pro
    [1199, 5000, 136, 5200],   # Sony Xperia 1 V
    [ 699, 4300, 130, 5600],   # Asus Zenfone 10
    [ 650, 4500, 133, 4800],   # Motorola Edge 50 Pro
    [ 599, 4700, 135, 4600],   # Nothing Phone (2)
    [ 999, 5600, 158, 7100],   # Honor Magic 6 Pro
], dtype=float)


# =============================================================================
# Run TOPSIS & Print Full Report
# =============================================================================

if __name__ == "__main__":

    print("\n" + "#" * 70)
    print("  TOPSIS METHOD — MAX NORMALIZATION")
    print("  Custom Python Implementation (no external TOPSIS library)")
    print("#" * 70)

    # ── Problem description ──────────────────────────────────────────────────
    print("""
PROBLEM DESCRIPTION
-------------------
Objective  : Choose the best smartphone from 10 modern alternatives.
Method     : TOPSIS (Technique for Order Preference by Similarity to
             Ideal Solution) — implemented from scratch using NumPy only.

CRITERIA & WEIGHTS
------------------
  Price (USD)      — weight 0.35 — MIN  (lower price is better)
  Battery (mAh)    — weight 0.15 — MAX  (higher capacity is better)
  Camera Score     — weight 0.25 — MAX  (DxOMark-equivalent score)
  Performance Score— weight 0.25 — MAX  (Geekbench 6 Multi-Core approx.)

DATA SOURCES
------------
  Prices          : Official launch prices / GSMArena
  Battery         : Manufacturer specifications / GSMArena
  Camera Score    : DxOMark / community benchmarks
  Performance     : Geekbench 6 Multi-Core (approximate)
""")

    # ── Step 0 — Initial Decision Matrix ────────────────────────────────────
    print_matrix(X, alternatives, criteria, "STEP 0 — Initial Decision Matrix  X")

    # ── Run algorithm ────────────────────────────────────────────────────────
    res = topsis(X, w, criteria_types)

    # ── Step 1 — Normalised Matrix ───────────────────────────────────────────
    print_matrix(res['normalized_matrix'], alternatives, criteria,
                 "STEP 1 — Max Normalized Matrix R\n"
                 "  r_ij = x_ij / max")

    # ── Step 2 — Weighted Normalised Matrix ──────────────────────────────────
    print_matrix(res['weighted_matrix'], alternatives, criteria,
                 "STEP 2 — Weighted Normalized Matrix  V = R × w")

    # ── Step 3 — Ideal & Anti-Ideal Solutions ────────────────────────────────
    print(f"\n{'='*70}")
    print("  STEP 3 — Ideal (A+) and Anti-Ideal (A-) Solutions")
    print(f"{'='*70}")
    col_w, row_w = 16, 28
    header = f"{'Solution':<{row_w}}" + "".join(f"{c:>{col_w}}" for c in criteria)
    print(header)
    print("-" * len(header))
    print_row(res['ideal_solution'],       criteria, "Ideal  (A+)", row_w, col_w)
    print_row(res['anti_ideal_solution'],  criteria, "Anti-Ideal (A-)", row_w, col_w)

    # ── Step 4 — Distances ───────────────────────────────────────────────────
    print(f"\n{'='*70}")
    print("  STEP 4 — Euclidean Distances from A+ and A-")
    print(f"{'='*70}")
    dist_cols = ["d+ (to Ideal)", "d- (to Anti-Ideal)"]
    dist_data = np.column_stack((res['distances_ideal'], res['distances_anti_ideal']))
    print_matrix(dist_data, alternatives, dist_cols, "Distances")

    # ── Step 5 — Closeness Coefficient & Ranking ─────────────────────────────
    print(f"\n{'='*70}")
    print("  STEP 5 — Closeness Coefficient  Ci  &  Final Ranking")
    print(f"  Ci = d- / (d+ + d-)    |  Ci ∈ [0,1],  higher = better")
    print(f"{'='*70}")
    print(f"\n  {'Alternative':<28} {'Ci':>10}  {'Rank':>6}")
    print("  " + "-" * 46)
    for i, alt in enumerate(alternatives):
        ci   = res['preference_vector'][i]
        rank = res['ranking_vector'][i]
        print(f"  {alt:<28} {ci:>10.4f}  {rank:>6}")

    # ── Step 6 — Final Sorted Order ──────────────────────────────────────────
    print(f"\n{'='*70}")
    print("  STEP 6 — Final Ranking (Best → Worst)")
    print(f"{'='*70}")
    for position, idx in enumerate(res['sorted_indices'], start=1):
        alt = alternatives[idx]
        ci  = res['preference_vector'][idx]
        print(f"  {position:>2}. {alt:<28}  Ci = {ci:.4f}")

    # ── Interpretation ───────────────────────────────────────────────────────
    best_idx  = res['sorted_indices'][0]
    worst_idx = res['sorted_indices'][-1]
    best_alt  = alternatives[best_idx]
    worst_alt = alternatives[worst_idx]
    best_ci   = res['preference_vector'][best_idx]
    worst_ci  = res['preference_vector'][worst_idx]

    print(f"""
{'='*70}
  INTERPRETATION
{'='*70}

  Best alternative  : {best_alt}  (Ci = {best_ci:.4f})
  Worst alternative : {worst_alt}  (Ci = {worst_ci:.4f})

  The TOPSIS method selected the {best_alt} as the best overall choice.
  Despite the heavy emphasis on price (weight = 0.35), it achieved rank #1
  by delivering a strong balance across all four criteria — competitive
  pricing, solid battery, a good camera score, and high performance.

  The {worst_alt} ranked last mainly because its high price relative
  to its performance and battery pushed it closest to the anti-ideal
  solution and farthest from the ideal.

  Note: Changing the weights will change the ranking. TOPSIS is
  sensitive to weight choices, which is a known limitation of the method.
{'='*70}
""")