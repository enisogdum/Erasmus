import importlib
Interval = importlib.import_module('1').Interval

def f1(X):
    return X * X - X

def f2(X):
    return X * (X - Interval(1, 1))

X = Interval(1, 2)
print("f1(X) =", f1(X), "; width =", f1(X).width())
print("f2(X) =", f2(X), "; width =", f2(X).width())