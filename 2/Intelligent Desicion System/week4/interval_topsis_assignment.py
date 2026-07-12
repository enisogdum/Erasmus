import numpy as np

def interval_topsis(X_L, X_U, w, criteria_types):
    m, n = X_L.shape
    V_L = np.zeros((m, n))
    V_U = np.zeros((m, n))
    
    for j in range(n):
        if np.allclose(X_L[:, j], X_U[:, j]):
            denom = np.sqrt(np.sum(X_L[:, j]**2))
        else:
            denom = np.sqrt(np.sum(X_L[:, j]**2 + X_U[:, j]**2))
            
        V_L[:, j] = w[j] * X_L[:, j] / denom
        V_U[:, j] = w[j] * X_U[:, j] / denom
        
    R_L = np.zeros(m)
    R_U = np.zeros(m)
    
    for k in range(m):
        A_plus_u = np.zeros(n)
        A_plus_l = np.zeros(n)
        A_minus_u = np.zeros(n)
        A_minus_l = np.zeros(n)
        
        for j in range(n):
            if criteria_types[j] == 'max':
                A_plus_u[j] = np.max(V_U[:, j])
                A_plus_l[j] = np.max([V_U[i, j] if i != k else V_L[k, j] for i in range(m)])
                A_minus_u[j] = np.min([V_L[i, j] if i != k else V_U[k, j] for i in range(m)])
                A_minus_l[j] = np.min(V_L[:, j])
            else: # min
                A_plus_u[j] = np.min(V_L[:, j])
                A_plus_l[j] = np.min([V_L[i, j] if i != k else V_U[k, j] for i in range(m)])
                A_minus_u[j] = np.max([V_U[i, j] if i != k else V_L[k, j] for i in range(m)])
                A_minus_l[j] = np.max(V_U[:, j])
                
        d_plus_u_sq = 0
        d_plus_l_sq = 0
        d_minus_u_sq = 0
        d_minus_l_sq = 0
        
        for j in range(n):
            if criteria_types[j] == 'max':
                d_plus_u_sq += (A_plus_u[j] - V_L[k, j])**2
                d_plus_l_sq += (A_plus_l[j] - V_U[k, j])**2
                d_minus_u_sq += (A_minus_l[j] - V_U[k, j])**2
                d_minus_l_sq += (A_minus_u[j] - V_L[k, j])**2
            else: # min
                d_plus_u_sq += (A_plus_u[j] - V_U[k, j])**2
                d_plus_l_sq += (A_plus_l[j] - V_L[k, j])**2
                d_minus_u_sq += (A_minus_l[j] - V_L[k, j])**2
                d_minus_l_sq += (A_minus_u[j] - V_U[k, j])**2
                
        d_plus_u = np.sqrt(d_plus_u_sq)
        d_plus_l = np.sqrt(d_plus_l_sq)
        d_minus_u = np.sqrt(d_minus_u_sq)
        d_minus_l = np.sqrt(d_minus_l_sq)
        
        R_L[k] = d_minus_l / (d_minus_u + d_plus_u)
        R_U[k] = d_minus_u / (d_minus_l + d_plus_l)
        
    return R_L, R_U

def evaluate(R_L, R_U, alt_names):
    mid = (R_L + R_U) / 2
    width = (R_U - R_L) / 2
    ranks = np.argsort(mid)[::-1]
    
    print(f"{'Alternative':<20} | {'Efficiency Interval':<20} | {'Mid-point':<10} | {'Half-width':<10} | {'Rank'}")
    print("-" * 75)
    for pos, idx in enumerate(ranks):
        print(f"{alt_names[idx]:<20} | [{R_L[idx]:.3f}, {R_U[idx]:.3f}]{'':<5} | {mid[idx]:.3f}      | {width[idx]:.3f}      | {pos+1}")

print("=== PART 1: Reproduce Article Example ===")
X_L_p1 = np.array([
    [1451, 2551, 40, 153],
    [843,  3742, 63, 459],
    [1125, 3312, 48, 153],
    [55,   5309, 72, 347],
    [356,  3709, 59, 151],
    [391,  4884, 72, 388]
])
X_U_p1 = np.array([
    [1451, 3118, 50, 187],
    [843,  4573, 77, 561],
    [1125, 4049, 58, 187],
    [55,   6488, 88, 426],
    [356,  4534, 71, 189],
    [391,  5969, 88, 474]
])
w_p1 = np.array([0.25, 0.25, 0.25, 0.25])
ctypes_p1 = ['min', 'min', 'max', 'max']
alt_p1 = [f"City {i}" for i in range(1, 7)]

rl, ru = interval_topsis(X_L_p1, X_U_p1, w_p1, ctypes_p1)
evaluate(rl, ru, alt_p1)


print("\n=== PART 2: Crisp Data into Intervals (Smartphone Data) ===")
crisp_X = np.array([
    [ 999, 3274, 154, 7192],
    [1299, 5000, 144, 7249],
    [ 999, 5050, 153, 4425],
    [ 799, 5400, 140, 6800],
    [ 899, 4880, 144, 6900],
    [1199, 5000, 136, 5200],
    [ 699, 4300, 130, 5600],
    [ 650, 4500, 133, 4800],
    [ 599, 4700, 135, 4600],
    [ 999, 5600, 158, 7100],
], dtype=float)
w_sp = np.array([0.35, 0.15, 0.25, 0.25])
ctypes_sp = ['min', 'max', 'max', 'max']
alt_sp = [
    "iPhone 15 Pro", "Samsung S24 Ultra", "Google Pixel 8 Pro", 
    "OnePlus 12", "Xiaomi 14 Pro", "Sony Xperia 1 V", 
    "Asus Zenfone 10", "Motorola Edge 50 Pro", "Nothing Phone (2)", 
    "Honor Magic 6 Pro"
]

print("--- Crisp TOPSIS ---")
rl_c, ru_c = interval_topsis(crisp_X, crisp_X, w_sp, ctypes_sp)
evaluate(rl_c, ru_c, alt_sp)

print("\n--- Interval TOPSIS (x_L = 0.95*x, x_U = 1.05*x) ---")
X_L_p2 = crisp_X * 0.95
X_U_p2 = crisp_X * 1.05
rl_2, ru_2 = interval_topsis(X_L_p2, X_U_p2, w_sp, ctypes_sp)
evaluate(rl_2, ru_2, alt_sp)


print("\n=== PART 3: Expert-based Intervals ===")
# Define realistic intervals based on market fluctuations or usage:
# Price: Can vary by $50 - $100 depending on sales
# Battery: Capacity degrades or varies slightly by batch (say -2% to +1%)
# Camera Score: Subjective, can be +/- 3 points based on lighting
# Perf Score: Thermal throttling can reduce score by up to 10%

X_L_p3 = crisp_X.copy()
X_U_p3 = crisp_X.copy()

# Price (discounted as low, MSRP as high)
X_L_p3[:, 0] = crisp_X[:, 0] - 100 # $100 discount
X_U_p3[:, 0] = crisp_X[:, 0] + 50  # slight inflation / tax differences

# Battery (degradation lower bound, marketed upper bound)
X_L_p3[:, 1] = crisp_X[:, 1] * 0.95
X_U_p3[:, 1] = crisp_X[:, 1]

# Camera
X_L_p3[:, 2] = crisp_X[:, 2] - 3
X_U_p3[:, 2] = crisp_X[:, 2] + 3

# Performance (throttled lower bound, max potential upper bound)
X_L_p3[:, 3] = crisp_X[:, 3] * 0.85
X_U_p3[:, 3] = crisp_X[:, 3]

rl_3, ru_3 = interval_topsis(X_L_p3, X_U_p3, w_sp, ctypes_sp)
evaluate(rl_3, ru_3, alt_sp)
