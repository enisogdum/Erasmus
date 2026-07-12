import importlib
# Import the Interval class from 1.py
Interval = importlib.import_module('1').Interval

X = Interval(-1, 2)
Y = Interval(3, 5)
print("X + Y =", X + Y)
print("X - Y =", X - Y)
print("X * Y =", X * Y)
print("width of X =", X.width())
print("midpoint of X =", X.mid())