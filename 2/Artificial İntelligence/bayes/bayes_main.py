import numpy as np
from bayes import NBCDiscrete

def read_wine_data(filepath):
    D = np.genfromtxt("wine.data", delimiter=",")
    y = D[:, 0].astype(np.int8)
    X = D[:, 1:]
    return X, y

def train_test_split(X, y, train_ratio=0.75):
    m, _ = X.shape
    indexes = np.random.permutation(m)
    X = X[indexes] # reindex rows according to random permutation
    y = y[indexes] # reindex rows according to random permutation
    threshold = round(train_ratio * m)
    X_train = X[:threshold]
    y_train = y[:threshold]
    X_test = X[threshold:]
    y_test = y[threshold:]
    return X_train, y_train, X_test, y_test

def discretize(X, bins, mins_ref=None, maxes_ref=None):
    if mins_ref is None:
        mins_ref = np.min(X, axis=0)
        maxes_ref = np.max(X, axis=0)
    X_d = np.clip(((X - mins_ref) / (maxes_ref - mins_ref) * bins).astype(np.int8), 0, bins - 1)
    return X_d, mins_ref, maxes_ref  
    
if __name__ == '__main__':
    print("BAYES...")
    seed = 3
    bins = 5
    np.random.seed(seed)
    X, y = read_wine_data("wine.data")
    X_train, y_train, X_test, y_test = train_test_split(X, y, train_ratio=0.75)
    X_train_d, mins_ref, maxes_ref = discretize(X_train, bins)
    X_test_d, _, _ = discretize(X_test, bins, mins_ref, maxes_ref)
        
    domain_sizes = bins * np.ones(X_train_d.shape[1], dtype=np.int8) # [5, 5, ..., 5] x 13 for wine data
    clf = NBCDiscrete(domain_sizes, laplace=True) 
    clf.fit(X_train_d, y_train)
    predictions_train = clf.predict(X_train_d)
    predictions_test = clf.predict(X_test_d)
    acc_train = np.mean(predictions_train == y_train) # same as: clf.score(X_train_d, y_train) 
    acc_test = np.mean(predictions_test == y_test) # same as: clf.score(X_test_d, y_test)
    print(f"ACCs, TRAIN: {acc_train} TEST: {acc_test}")    
    print("BAYES DONE.")
    