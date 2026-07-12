import numpy as np
from scipy.optimize import linprog

def calculate_bwm(input_vector):
    """
    Calculates weights using the Best-Worst Method (BWM) based on linear programming.
    This implementation handles vectors of any length.
    
    Args:
        input_vector (list or numpy.ndarray): The input vector of length N.
        
    Returns:
        tuple: A tuple containing:
            - original input_vector
            - BWM weight vector (list)
            - xi value (float)
            - MAE value (float)
    """
    w_input = np.array(input_vector, dtype=float)
    
    # 1. Normalize the vector to sum to 1 to get reference weights
    total_sum = np.sum(w_input)
    if total_sum == 0:
        raise ValueError("Input vector sum is 0, cannot normalize.")
    w_ref = w_input / total_sum
    n = len(w_ref)
    
    # 2. Identify Best and Worst criteria
    B = np.argmax(w_ref)
    W = np.argmin(w_ref)
    
    # 3. Calculate Saaty scale values (1-9)
    # The reference values are w_ref[B] / w_ref[j] for Best-to-others
    # and w_ref[j] / w_ref[W] for Others-to-worst.
    # We round them to nearest integer and clip them to the 1-9 scale.
    a_B = []
    a_W = []
    for j in range(n):
        # Best to others (a_Bj)
        if w_ref[j] == 0:
            val_B = 9
        else:
            val_B = int(np.round(w_ref[B] / w_ref[j]))
        a_B.append(min(max(val_B, 1), 9))
        
        # Others to worst (a_jW)
        if w_ref[W] == 0:
            val_W = 9 if w_ref[j] > 0 else 1
        else:
            val_W = int(np.round(w_ref[j] / w_ref[W]))
        a_W.append(min(max(val_W, 1), 9))
        
    # 4. Set up the linear programming formulation
    # Variables: w_0, w_1, ..., w_{n-1}, xi
    # Objective: min xi
    c = np.zeros(n + 1)
    c[-1] = 1  
    
    A_ub = []
    b_ub = []
    
    # Constraints: |w_B - a_Bj * w_j| <= xi
    # This translates to:
    # w_B - a_Bj * w_j - xi <= 0
    # -w_B + a_Bj * w_j - xi <= 0
    for j in range(n):
        if j == B:
            continue
            
        row1 = np.zeros(n + 1)
        row1[B] = 1
        row1[j] = -a_B[j]
        row1[-1] = -1
        A_ub.append(row1)
        
        row2 = np.zeros(n + 1)
        row2[B] = -1
        row2[j] = a_B[j]
        row2[-1] = -1
        A_ub.append(row2)
        
    # Constraints: |w_j - a_jW * w_W| <= xi
    # This translates to:
    # w_j - a_jW * w_W - xi <= 0
    # -w_j + a_jW * w_W - xi <= 0
    for j in range(n):
        if j == W:
            continue
            
        row1 = np.zeros(n + 1)
        row1[j] = 1
        row1[W] = -a_W[j]
        row1[-1] = -1
        A_ub.append(row1)
        
        row2 = np.zeros(n + 1)
        row2[j] = -1
        row2[W] = a_W[j]
        row2[-1] = -1
        A_ub.append(row2)
        
    # Equality constraint: sum of w_j = 1
    A_eq = [np.append(np.ones(n), 0)]
    b_eq = [1]
    
    # Variable bounds: w_j >= 0, xi >= 0
    bounds = [(0, None) for _ in range(n + 1)]
    
    # 5. Solve linear programming formulation
    res = linprog(c, A_ub=A_ub, b_ub=np.zeros(len(A_ub)), A_eq=A_eq, b_eq=b_eq, bounds=bounds, method='highs')
    
    if not res.success:
        raise ValueError("Linear programming did not converge: " + res.message)
        
    w_bwm = res.x[:-1]
    xi = res.x[-1]
    
    # 6. Calculate MAE between reference weights and BWM weights
    mae = np.sum(np.abs(w_ref - w_bwm)) / n
    
    return input_vector, w_bwm.tolist(), float(xi), float(mae)

if __name__ == "__main__":
    # Example usage demonstrating vectors of any length (Grade 5 requirement)
    test_vectors = [
        [10, 20, 30],                  # Length 3
        [10, 20, 30, 40],              # Length 4
        [5, 15, 25, 35, 20],           # Length 5
        [0.1, 0.2, 0.05, 0.3, 0.15, 0.2] # Length 6
    ]
    
    for i, test_vector in enumerate(test_vectors, 1):
        inp, w_out, xi_val, mae_val = calculate_bwm(test_vector)
        print(f"--- Test Case {i} (Length: {len(test_vector)}) ---")
        print("Input Vector:", inp)
        print("BWM Weights:", [round(w, 4) for w in w_out])
        print("Xi:", round(xi_val, 4))
        print("MAE:", round(mae_val, 4))
        print()
