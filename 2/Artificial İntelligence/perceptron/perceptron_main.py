import numpy as np
from matplotlib import pyplot as plt
from perceptron import SimplePerceptron
import time

def fake_data(m):
    x1 = np.random.rand(m, 1)
    m_half = m // 2
    x2_top = 0.499 * np.random.rand(m_half, 1) + 0.501
    x2_bottom = 0.499 * np.random.rand(m_half, 1)
    X = np.c_[x1, np.r_[x2_top, x2_bottom]]
    y = (np.r_[np.ones(m_half), -np.ones(m_half)]).astype(np.int8)
    return X, y

if __name__ == '__main__':
    print("SIMPLE PERCEPTRON...")
    # np.random.seed(0)
    X, y = fake_data(1000)
    clf = SimplePerceptron(learning_rate=0.1)
    print("FIT...")
    t1 = time.time()
    clf.fit(X, y)   
    t2 = time.time()
    print(f"FIT DONE. [time: {t2 - t1} s]")  
    print(clf.w_)
    print(clf.k_)
    
    plt.scatter(X[:, 0], X[:, 1], c=y, marker='.')    
    x1_args = np.array([0.0, 1.0])
    x2_vals = -(clf.w_[0] + clf.w_[1] * x1_args) / clf.w_[2]
    plt.plot(x1_args, x2_vals)
    plt.show()