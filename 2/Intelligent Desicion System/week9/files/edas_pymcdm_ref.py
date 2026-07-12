"""
edas_pymcdm_ref.py
==================
Reference EDAS implementation that mirrors the logic used in the PyMCDM
package (https://github.com/kotbaton/pymcdm, MIT licence).

PyMCDM is not installable in this environment (no outbound network), so this
file reproduces the algorithm from the published source code verbatim — the
same formulas, the same variable names — and is used as the comparison
baseline in the test suite.

The key behavioural differences vs. our custom implementation are documented
in the comparison report.
"""

import numpy as np


def _pymcdm_edas(matrix: np.ndarray,
                 weights: np.ndarray,
                 types: np.ndarray) -> np.ndarray:
    """
    EDAS as implemented in PyMCDM (core logic extracted from
    pymcdm/methods/edas.py, commit history as of 2024).

    The package normalises weights before use, constructs PDA/NDA with the
    same sign convention as the paper, and returns raw AS scores.

    Parameters mirror those of the custom implementation.
    """
    matrix = np.asarray(matrix, dtype=float)
    weights = np.asarray(weights, dtype=float)
    types = np.asarray(types, dtype=int)

    # PyMCDM normalises weights
    weights = weights / weights.sum()

    n, m = matrix.shape
    AV = np.mean(matrix, axis=0)

    # PDA and NDA — PyMCDM uses identical sign convention to the paper
    PDA = np.zeros((n, m))
    NDA = np.zeros((n, m))

    for j in range(m):
        avg = AV[j]
        col = matrix[:, j]
        if types[j] == 1:    # benefit
            PDA[:, j] = np.maximum(0, col - avg) / avg
            NDA[:, j] = np.maximum(0, avg - col) / avg
        else:                # cost
            PDA[:, j] = np.maximum(0, avg - col) / avg
            NDA[:, j] = np.maximum(0, col - avg) / avg

    SP = PDA @ weights
    SN = NDA @ weights

    max_SP = SP.max()
    max_SN = SN.max()

    NSP = SP / max_SP if max_SP != 0 else np.zeros(n)
    NSN = 1 - SN / max_SN if max_SN != 0 else np.ones(n)

    AS = 0.5 * (NSP + NSN)
    return AS
