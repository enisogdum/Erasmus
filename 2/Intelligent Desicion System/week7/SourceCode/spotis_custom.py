"""
spotis_custom.py
----------------
Custom implementation of the SPOTIS (Stable Preference Ordering Towards
Ideal Solution) method for Multi-Criteria Decision-Making, based on:

  Dezert, J., Tchamova, A., Han, D., Tacnet, J.-M. (2020).
  "The SPOTIS Rank Reversal Free Method for Multi-Criteria Decision-Making
  Support." 2020 IEEE 23rd International Conference on Information Fusion.

Author: Custom implementation for laboratory exercise.
"""

import numpy as np


class SPOTIS:
    """
    SPOTIS – Stable Preference Ordering Towards Ideal Solution.

    Parameters
    ----------
    score_matrix : array-like, shape (M, N)
        M alternatives × N criteria.
    weights : array-like, shape (N,)
        Importance weights for each criterion (will be normalised internally
        so they sum to 1).
    bounds : array-like, shape (N, 2)
        Each row is [min_j, max_j] – the absolute bounds of criterion j.
    types : array-like of int, shape (N,)
        +1  →  larger score is better (benefit criterion)
        -1  →  smaller score is better (cost criterion)
    """

    def __init__(self, score_matrix, weights, bounds, types):
        self.S = np.array(score_matrix, dtype=float)
        self.w = np.array(weights, dtype=float)
        self.w = self.w / self.w.sum()          # normalise
        self.bounds = np.array(bounds, dtype=float)   # shape (N, 2)
        self.types = np.array(types, dtype=int)

        self._validate()

    def _validate(self):
        M, N = self.S.shape
        assert self.w.shape == (N,), "weights length must equal number of criteria"
        assert self.bounds.shape == (N, 2), "bounds must have shape (N, 2)"
        assert self.types.shape == (N,), "types length must equal number of criteria"
        assert set(self.types).issubset({1, -1}), "types must be +1 or -1"

    def _ideal_solution(self):
        """Compute the Ideal Solution Point (ISP) from bounds and types."""
        isp = np.where(
            self.types == 1,
            self.bounds[:, 1],   # larger is better → use max bound
            self.bounds[:, 0],   # smaller is better → use min bound
        )
        return isp

    def _normalized_distances(self, isp):
        """
        Compute normalised distance matrix D (shape M × N).
        Formula (3) / (6) from the paper:
            d_ij = |S_ij - S*_j| / |S_max_j - S_min_j|
        """
        ranges = self.bounds[:, 1] - self.bounds[:, 0]   # S_max - S_min, shape (N,)
        D = np.abs(self.S - isp) / ranges                 # broadcast, shape (M, N)
        return D

    def _weighted_average_distances(self, D):
        """
        Compute per-alternative weighted average distance (Step 4).
        d(A_i, s*) = sum_j  w_j * d_ij
        """
        return D @ self.w                                 # shape (M,)

    def evaluate(self, isp=None):
        """
        Run SPOTIS and return a dict with all intermediate results.

        Parameters
        ----------
        isp : array-like or None
            Optional custom Ideal/Expected Solution Point.
            If None the standard ISP derived from bounds and types is used.

        Returns
        -------
        dict with keys:
            'isp'           – Ideal Solution Point used
            'distances'     – normalised distance matrix (M × N)
            'avg_distances' – weighted average distances (M,)
            'ranking'       – 1-based ranks (1 = best)
            'order'         – alternative indices sorted best→worst
        """
        if isp is None:
            isp = self._ideal_solution()
        else:
            isp = np.array(isp, dtype=float)

        D = self._normalized_distances(isp)
        d_avg = self._weighted_average_distances(D)

        order = np.argsort(d_avg)                        # indices best→worst
        ranks = np.empty_like(order)
        ranks[order] = np.arange(1, len(order) + 1)

        return {
            "isp": isp,
            "distances": D,
            "avg_distances": d_avg,
            "ranking": ranks,
            "order": order,
        }

    def rank(self, isp=None):
        """Return 1-based ranking array (convenience wrapper)."""
        return self.evaluate(isp=isp)["ranking"]
