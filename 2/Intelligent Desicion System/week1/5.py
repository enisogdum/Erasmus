import importlib
Interval = importlib.import_module('1').Interval

def inv(X):
    assert not (X.a <= 0 <= X.b), "Cannot invert an interval containing zero"
    return Interval(1 / X.b, 1 / X.a)

def div(X, Y):
    return X * inv(Y)

def newton(X):
    m = X.mid()
    f_m = m * m - 2
    derivative = Interval(2 * X.a, 2 * X.b)
    return Interval(m, m) - div(Interval(f_m, f_m), derivative)

X = Interval(1, 2)

for i in range(5):
    N = newton(X)
    X = Interval(max(X.a, N.a), min(X.b, N.b))
    print(f"iteration {i+1}: X = {X}, width = {X.width()}")
