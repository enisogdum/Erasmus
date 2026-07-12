import numpy as np
from sklearn.base import BaseEstimator, ClassifierMixin

class NBCDiscrete(BaseEstimator, ClassifierMixin):

    def __init__(self, domain_sizes, laplace=False, safe=False):
        self.domain_sizes = domain_sizes
        self.laplace = laplace
        self.safe = safe

    def fit(self, X, y):
        m, n = X.shape
        self.classes_ = np.unique(y)
        K = self.classes_.size

        y_mapped = np.empty(m, dtype=np.int32)
        self.PY_ = np.zeros(K)

        q_max = np.max(self.domain_sizes)
        self.P_ = np.zeros((K, n, q_max))

        #priors
        for k in range(K):
            mask = (y == self.classes_[k])
            y_mapped[mask] = k
            self.PY_[k] = np.mean(mask)

        #coount
        for i in range(m):
            for j in range(n):
                self.P_[y_mapped[i], j, X[i, j]] += 1

        #normalize (likelihood)
        for k in range(K):
            Nk = m * self.PY_[k]

            for j in range(n):
                if self.laplace:
                    self.P_[k, j, :self.domain_sizes[j]] = \
                        (self.P_[k, j, :self.domain_sizes[j]] + 1) / \
                        (Nk + self.domain_sizes[j])
                else:
                    self.P_[k, j, :self.domain_sizes[j]] /= Nk

        #precomputing logs (hocanın istediği)
        if self.safe:
            self.log_PY_ = np.log(self.PY_ + 1e-12)
            self.log_P_ = np.log(self.P_ + 1e-12)

        return self

    def predict(self, X):
        return self.classes_[np.argmax(self.predict_proba(X), axis=1)]

    def predict_proba(self, X):
        m, n = X.shape
        K = self.classes_.size

        if self.safe:
            probas = np.zeros((m, K))
        else:
            probas = np.ones((m, K))

        for i in range(m):
            x = X[i]
            for k in range(K):

                if self.safe:
                    probas[i, k] += self.log_PY_[k]
                else:
                    probas[i, k] *= self.PY_[k]

                for j in range(n):
                    if self.safe:
                        probas[i, k] += self.log_P_[k, j, x[j]]
                    else:
                        probas[i, k] *= self.P_[k, j, x[j]]

        return probas