import importlib
Interval = importlib.import_module('1').Interval

L = Interval(0.999, 1.001)
W = Interval(1.999, 2.001)
A = L * W
print("Area A =", A)
print("Width of the result =", A.width())
