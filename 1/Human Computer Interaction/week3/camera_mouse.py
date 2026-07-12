import cv2
import numpy as np
import pyautogui
import time

# --- Configuration ---
# Skin Color HSV Thresholds (Based on Week 2)
MIN_HSV = np.array([0, 51, 102], dtype=np.uint8)
MAX_HSV = np.array([18, 153, 255], dtype=np.uint8)
# For the second red range (162-179)
MIN_HSV2 = np.array([162, 51, 102], dtype=np.uint8)
MAX_HSV2 = np.array([179, 153, 255], dtype=np.uint8)

# Movement Smoothing Factor (0.0 to 1.0)
# Lower = smoother but more lag. Higher = faster but more jitter.
SMOOTHING_FACTOR = 0.2

# Sensitivity Scale (How much screen distance per camera pixel)
# Increase this to move the mouse further with smaller head movements.
SCALE_X = 2.0
SCALE_Y = 2.0 # Generally keep Y wider or same

# --- Global State ---
neutral_pos = None # (x, y) of the face when "centered"
prev_mouse_x, prev_mouse_y = pyautogui.position()

def get_biggest_contour_centroid(mask):
    """
    Finds the largest contour in the mask and returns its centroid.
    """
    contours, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    
    if not contours:
        return None, None
    
    # Find likely the face/hand (largest area)
    largest_contour = max(contours, key=cv2.contourArea)
    
    # Filter by area to avoid noise
    if cv2.contourArea(largest_contour) < 500:
        return None, None

    # Calculate Moments
    M = cv2.moments(largest_contour)
    if M["m00"] == 0:
        return None, None

    cx = int(M["m10"] / M["m00"])
    cy = int(M["m01"] / M["m00"])
    
    return (cx, cy), largest_contour

def main():
    global neutral_pos, prev_mouse_x, prev_mouse_y

    # 1. Screen Setup
    screen_w, screen_h = pyautogui.size()
    print(f"Screen Size: {screen_w}x{screen_h}")
    
    # Center of screen
    center_screen_x = screen_w // 2
    center_screen_y = screen_h // 2

    # 2. Camera Setup
    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        print("Error: Could not open camera.")
        return

    # Camera resolution
    cam_w = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    cam_h = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    print(f"Camera Resolution: {cam_w}x{cam_h}")

    print("--------------------------------------------------")
    print("Controls:")
    print("  'c' - Calibrate (Set current face position as neutral center)")
    print("  'q' - Quit")
    print("--------------------------------------------------")
    print("Please calibrate first by finding a comfortable position and pressing 'c'.")

    # Initialize previous mouse position to center
    prev_mouse_x, prev_mouse_y = center_screen_x, center_screen_y

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame.")
            break

        # Flip horizontally for mirror effect (natural movement)
        frame = cv2.flip(frame, 1)

        # 3. Processing
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

        # Create Masks (H < 18 OR H > 162) AND (S...) AND (V...)
        mask1 = cv2.inRange(hsv, MIN_HSV, MAX_HSV)
        mask2 = cv2.inRange(hsv, MIN_HSV2, MAX_HSV2)
        skin_mask = mask1 | mask2

        # Morphological Operations to clean noise (Open/Close)
        kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
        skin_mask = cv2.morphologyEx(skin_mask, cv2.MORPH_OPEN, kernel, iterations=2)
        skin_mask = cv2.morphologyEx(skin_mask, cv2.MORPH_DILATE, kernel, iterations=1)

        # 4. Tracking
        centroid, contour = get_biggest_contour_centroid(skin_mask)

        # 5. Output Visualization
        display_frame = frame.copy()

        if centroid:
            cx, cy = centroid
            
            # Draw visual feedback
            cv2.circle(display_frame, (cx, cy), 5, (0, 0, 255), -1)
            if contour is not None:
                x, y, w, h = cv2.boundingRect(contour)
                cv2.rectangle(display_frame, (x, y), (x+w, y+h), (0, 255, 0), 2)

            # 6. Mouse Control Logic
            if neutral_pos:
                nx, ny = neutral_pos
                
                # Calculate delta from neutral position
                # Note: moving head RIGHT (positive x) -> Mouse RIGHT
                # moving head DOWN (positive y) -> Mouse DOWN
                dx = cx - nx
                dy = cy - ny
                
                # Calculate target mouse position
                # We add the delta * scale to the screen center
                target_x = center_screen_x + (dx * SCALE_X)
                target_y = center_screen_y + (dy * SCALE_Y)
                
                # Clamp to screen bounds
                target_x = max(0, min(screen_w - 1, target_x))
                target_y = max(0, min(screen_h - 1, target_y))

                # Smoothing (Linear Interpolation)
                # new_pos = prev + (target - prev) * factor
                curr_mouse_x = prev_mouse_x + (target_x - prev_mouse_x) * SMOOTHING_FACTOR
                curr_mouse_y = prev_mouse_y + (target_y - prev_mouse_y) * SMOOTHING_FACTOR

                # Move Mouse
                # Fail-safe: moving mouse to corners usually aborts pyautogui script.
                # We clamp it, but be careful.
                try:
                    pyautogui.moveTo(curr_mouse_x, curr_mouse_y)
                except pyautogui.FailSafeException:
                    pass # unexpected movement to corner

                prev_mouse_x, prev_mouse_y = curr_mouse_x, curr_mouse_y

                # Draw a line from neutral to current for visualising vector
                cv2.line(display_frame, neutral_pos, (cx, cy), (255, 255, 0), 2)

        if neutral_pos:
            cv2.circle(display_frame, neutral_pos, 5, (255, 0, 0), -1)
            cv2.putText(display_frame, "CALIBRATED", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
        else:
             cv2.putText(display_frame, "PRESS 'c' TO CALIBRATE", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)


        # Show windows
        cv2.imshow('Camera Mouse', display_frame)
        cv2.imshow('Mask', skin_mask)

        # Input handling
        key = cv2.waitKey(1) & 0xFF
        if key == ord('q'):
            break
        elif key == ord('c'):
            if centroid:
                neutral_pos = centroid
                print(f"Calibrated Neutral Position: {neutral_pos}")
            else:
                print("Cannot calibrate: No face/hand detected!")

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
