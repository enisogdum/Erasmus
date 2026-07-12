import numpy as np
from sklearn.base import BaseEstimator, ClassifierMixin

class SimplePerceptron(BaseEstimator, ClassifierMixin):

    def __init__(self, learning_rate=0.1, k_max=None):
        self.learning_rate = learning_rate
        self.k_max = k_max  # max training steps; None = unlimited (until convergence)
    
    def fit(self, X, y):
        np.random.seed(0)
        self.classes_ = np.unique(y) # we assume exactly 2 classes
        m, n = X.shape
        y_mapped = np.ones(m, dtype=np.int8)
        y_mapped[y == self.classes_[0]] = -1 # we assume the firt class is "-1"        
        self.w_ = np.zeros(n + 1) # vector of weights
        self.k_ = 0 # steps counter (for information only)
        X_ext = np.c_[np.ones(m), X] # we prepend intercepts (biases)        
        while True:
            # Stop if we hit the step limit
            if self.k_max is not None and self.k_ >= self.k_max:
                break
            s = self.w_.dot(X_ext.T) # weighted sums for all data points
            error_indexes = np.where(s * y_mapped <= 0.0)[0]
            if len(error_indexes) == 0: # stop condition (convergence)
                break
            i = np.random.choice(error_indexes)
            x_i = X_ext[i]
            y_i = y_mapped[i]
            self.w_ = self.w_ + self.learning_rate * y_i * x_i
            self.k_ += 1
            
    def predict(self, X, y=None):
        scores = self.decision_function(X)
        # scores > 0  --> mapped class +1 --> original classes_[1]
        # scores <= 0 --> mapped class -1 --> original classes_[0]
        return np.where(scores > 0, self.classes_[1], self.classes_[0])

    def decision_function(self, X, y=None):
        # Prepend bias column (intercept) just like in fit
        X_ext = np.c_[np.ones(X.shape[0]), X]
        return X_ext.dot(self.w_)  # raw weighted sums (one per sample)