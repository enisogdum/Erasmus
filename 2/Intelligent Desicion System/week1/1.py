class Interval:
    def __init__(self, a, b):
        # Handle minor floating point precision crossovers near zero-width
        if a > b and abs(a - b) < 1e-9:
            a = b
        assert a <= b, "The left endpoint must be <= the right endpoint"
        self.a = a
        self.b = b
    def __repr__(self):
        return f"[{self.a}, {self.b}]"
    def width(self):
        return self.b - self.a
    def mid(self):
        return (self.a + self.b) / 2
    def __add__(self, other):
        return Interval(self.a + other.a, self.b + other.b)
    def __sub__(self, other):
        return Interval(self.a - other.b, self.b - other.a)
    def __mul__(self, other):
        vals = [
            self.a * other.a,
            self.a * other.b,
            self.b * other.a,
            self.b * other.b,
        ]
        return Interval(min(vals), max(vals))

if __name__ == "__main__":
    # Example usage
    i1 = Interval(1, 4)
    i2 = Interval(2, 5)
    
    print(f"Interval 1: {i1}")
    print(f"Interval 2: {i2}")
    print(f"i1 width: {i1.width()}")
    print(f"i1 mid: {i1.mid()}")
    print(f"i1 + i2: {i1 + i2}")
    print(f"i1 - i2: {i1 - i2}")
    print(f"i1 * i2: {i1 * i2}")