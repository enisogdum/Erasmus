"""
edas_custom.py
==============
Custom implementation of the EDAS (Evaluation Based on Distance from Average
Solution) method as described in:

    Keshavarz Ghorabaee et al. (2015). Multi-Criteria Inventory Classification
    Using a New Method of Evaluation Based on Distance from Average Solution
    (EDAS). Informatica, 26(3), 435-451.

Steps
-----
1.  Build the decision matrix X [n × m].
2.  Compute the average solution AV_j = mean of each column.
3.  Compute PDA and NDA matrices according to criterion type
    (beneficial / non-beneficial).
4.  Compute weighted sums SP_i and SN_i.
5.  Normalise to NSP_i and NSN_i.
6.  Compute appraisal score AS_i = 0.5 * (NSP_i + NSN_i).
7.  Rank alternatives by descending AS.
"""

import numpy as np


class EDAS:
    """
    Evaluation Based on Distance from Average Solution.

    Parameters
    ----------
    verbose : bool
        If True, intermediate matrices are stored as attributes after calling
        ``__call__``.
    """

    def __init__(self, verbose: bool = False):
        self.verbose = verbose

    # ------------------------------------------------------------------
    def __call__(
        self,
        matrix: np.ndarray,
        weights: np.ndarray,
        types: np.ndarray,
    ) -> np.ndarray:
        """
        Run the EDAS procedure.

        Parameters
        ----------
        matrix : np.ndarray, shape (n, m)
            Decision matrix where rows are alternatives and columns are
            criteria.
        weights : np.ndarray, shape (m,)
            Criterion weights.  Need not sum to 1; they are normalised
            internally so the method is weight-scale invariant.
        types : np.ndarray, shape (m,)
            +1 for a beneficial criterion, -1 for a non-beneficial criterion.

        Returns
        -------
        np.ndarray, shape (n,)
            Appraisal scores AS in [0, 1].  Higher is better.
        """
        matrix = np.asarray(matrix, dtype=float)
        weights = np.asarray(weights, dtype=float)
        types = np.asarray(types, dtype=int)

        n, m = matrix.shape
        assert weights.shape == (m,), "weights length must equal number of criteria"
        assert types.shape == (m,), "types length must equal number of criteria"

        # ── Step 3: Average solution ──────────────────────────────────
        AV = matrix.mean(axis=0)          # shape (m,)

        # ── Step 4: PDA and NDA ───────────────────────────────────────
        PDA = np.zeros((n, m))
        NDA = np.zeros((n, m))

        for j in range(m):
            diff = matrix[:, j] - AV[j]  # shape (n,)
            if types[j] == 1:            # beneficial
                PDA[:, j] = np.maximum(0,  diff) / AV[j]
                NDA[:, j] = np.maximum(0, -diff) / AV[j]
            else:                        # non-beneficial
                PDA[:, j] = np.maximum(0, -diff) / AV[j]
                NDA[:, j] = np.maximum(0,  diff) / AV[j]

        # ── Step 5: Weighted sums ─────────────────────────────────────
        w = weights / weights.sum()       # normalise weights
        SP = PDA @ w                      # shape (n,)
        SN = NDA @ w                      # shape (n,)

        # ── Step 6: Normalise SP and SN ───────────────────────────────
        NSP = SP / SP.max() if SP.max() != 0 else np.zeros(n)
        NSN = 1 - SN / SN.max() if SN.max() != 0 else np.ones(n)

        # ── Step 7: Appraisal score ───────────────────────────────────
        AS = 0.5 * (NSP + NSN)

        if self.verbose:
            self.AV_ = AV
            self.PDA_ = PDA
            self.NDA_ = NDA
            self.SP_ = SP
            self.SN_ = SN
            self.NSP_ = NSP
            self.NSN_ = NSN
            self.AS_ = AS

        return AS

    # ------------------------------------------------------------------
    @staticmethod
    def rank(scores: np.ndarray) -> np.ndarray:
        """Return ranks (1 = best) for an array of appraisal scores."""
        order = np.argsort(-scores)       # descending
        ranks = np.empty_like(order)
        ranks[order] = np.arange(1, len(scores) + 1)
        return ranks
