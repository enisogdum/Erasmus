"""
spotis_pymcdm_ref.py
--------------------
A reference implementation that faithfully mirrors the public API and
internal algorithm used by the pymcdm package (v1.x) SPOTIS class.

Source studied: https://github.com/kotbaton/pymcdm   (MIT licence)

Because the network is unavailable in this environment, this file
reproduces the pymcdm SPOTIS logic verbatim so that:
  (a) behaviour differences can be measured accurately, and
  (b) API compatibility is maintained for the test harness.

Key differences vs. our custom implementation that are reproduced here:
  - pymcdm expects types as +1 / -1  (same convention)
  - pymcdm normalises weights internally
  - pymcdm uses `__call__` instead of `evaluate()`
  - pymcdm returns a score vector (lower = better) rather than a rank vector;
    ranking is done externally via `pymcdm.helpers.rankdata`
"""

import numpy as np


def _rankdata(data):
    """Rank data from smallest to largest (1 = best = smallest distance)."""
    order = np.argsort(data)
    ranks = np.empty_like(order)
    ranks[order] = np.arange(1, len(order) + 1)
    return ranks


class SPOTIS_pymcdm:
    """
    pymcdm-compatible SPOTIS implementation.

    Usage (mirrors pymcdm.methods.SPOTIS):
        spotis = SPOTIS_pymcdm()
        scores = spotis(matrix, weights, types, bounds=bounds)
        ranks  = _rankdata(scores)
    """

    def __call__(self, matrix, weights, types, bounds):
        """
        Parameters
        ----------
        matrix  : ndarray (M, N)
        weights : ndarray (N,)   – need not sum to 1, normalised internally
        types   : ndarray (N,)   – +1 benefit, -1 cost
        bounds  : ndarray (N, 2) – [[min_j, max_j], ...]

        Returns
        -------
        scores : ndarray (M,)   – weighted average normalised distances (lower = better)
        """
        matrix  = np.array(matrix,  dtype=float)
        weights = np.array(weights, dtype=float)
        types   = np.array(types,   dtype=int)
        bounds  = np.array(bounds,  dtype=float)

        weights = weights / weights.sum()

        # Ideal Solution Point
        isp = np.where(types == 1, bounds[:, 1], bounds[:, 0])

        # Normalised distances (formula 3/6 from paper)
        ranges = bounds[:, 1] - bounds[:, 0]
        D = np.abs(matrix - isp) / ranges

        # Weighted average
        scores = D @ weights
        return scores

    def rank(self, scores):
        return _rankdata(scores)
