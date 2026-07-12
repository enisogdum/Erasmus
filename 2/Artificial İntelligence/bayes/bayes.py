import numpy as np
try:
    from sklearn.base import BaseEstimator, ClassifierMixin
except ImportError:
    class BaseEstimator:
        pass
    class ClassifierMixin:
        def score(self, X, y):
            return np.mean(self.predict(X) == y)

class NBCDiscrete(BaseEstimator, ClassifierMixin):

    def __init__(self, domain_sizes, laplace=False):
        self.domain_sizes = domain_sizes 
        self.laplace = laplace
    
    def fit(self, X, y):
        m, n = X.shape
        self.classes_ = np.unique(y)
        K = self.classes_.size # K - number of distinct classes 
        y_mapped = np.empty(m, dtype=np.int8)
        self.PY_ = np.zeros(K)
        q_max = np.max(self.domain_sizes)
        for k in range(K):
            flags_of_class_k = y == self.classes_[k]
            y_mapped[flags_of_class_k] = k
            self.PY_[k] = np.mean(flags_of_class_k)
        self.P_ = np.zeros((K, n, q_max))
        for i in range(m):
            x = X[i] # e.g., x = [4, 3, 3, 4, 1, ...], yy = 2
            yy = y_mapped[i]
            for j in range(n):
                self.P_[yy, j, x[j]] += 1
        for k in range(K):
            if not self.laplace:
                self.P_[k] /= m * self.PY_[k]
            else:
                for j in range(n):
                    self.P_[k, j] = (self.P_[k, j] + 1) / (m * self.PY_[k] + self.domain_sizes[j]) 
    
    def predict(self, X):        
        return self.classes_[np.argmax(self.predict_proba(X), axis=1)]
    
    def predict_proba(self, X):
        m, n = X.shape # now m is just count of data points to be tested
        K = self.classes_.size
        probas = np.ones((m, K))
        for i in range(m):
            x = X[i]
            for k in range(K):
                probas[i, k] *= self.PY_[k]
                for j in range(n):
                    probas[i, k] *= self.P_[k, j, x[j]]
        return probas        