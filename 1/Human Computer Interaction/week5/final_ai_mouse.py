import cv2
import mediapipe as mp
import pyautogui
import math
import collections
import time
import os

# Disable FailSafe
pyautogui.FAILSAFE = False

# --- Configuration ---
CLICK_THRESHOLD = 0.05  # Distance threshold for pinch
PUSH_THRESHOLD = -0.15  # Z-value threshold for push (negative is closer)
PUSH_HYSTERESIS = 0.05  # Reset buffer for push
SMOOTHING = 0.7         # 0.0 to 1.0 (Higher = more smoothing? No, in this code higher = faster/less smooth. 
# Formula: cur = prev + (target - prev) * SMOOTHING
# 0.1: Moves 10% towards target (Very smooth/laggy)
# 0.9: Moves 90% towards target (Instant/Jittery)
# Increasing to 0.7 for snappier response.

MOUSE_SENSITIVITY = 1.5 # 1.0 = Default. >1.0 = More sensitive (less hand movement needed).

# --- Terminal Instructions ---
def print_instructions():
    print("="*60)
    print("   WEEK 5: FINAL AI MOUSE CONTROLLER")
    print("="*60)
    print("GESTURE GUIDE:")
    print("1. CURSOR MOVEMENT:")
    print("   - Point with your INDEX FINGER.")
    print("   - The cursor follows your index finger tip.")
    print("")
    print("2. LEFT CLICK:")
    print("   - PINCH your INDEX FINGER and THUMB together.")
    print("   - Short tap for click, hold for drag.")
    print("")
    print("3. RIGHT CLICK:")
    print("   - PINCH your MIDDLE FINGER and THUMB together.")
    print("")
    print("4. FULL SCREENSHOT (Ring+Thumb):")
    print("   - PINCH your RING FINGER and THUMB together.")
    print("   - Saves FULL SCREEN as 'full_screenshot_TIMESTAMP.png' to DESKTOP.")
    print("")
    print("5. PUSH CLICK (Experimental):")
    print("   - Move your open hand quickly TOWARDS the camera.")
    print("   - Useful for 'air tapping'.")
    print("="*60)
    print("Press 'q' to quit the application.")
    print("="*60)
    time.sleep(2) # Give user time to read

def calculate_distance(p1, p2):
    return math.hypot(p1.x - p2.x, p1.y - p2.y)

def main():
    print_instructions()

    mp_hands = mp.solutions.hands
    mp_drawing = mp.solutions.drawing_utils
    screen_width, screen_height = pyautogui.size()
    cap = cv2.VideoCapture(0)

    # State variables
    left_click_active = False
    right_click_active = False
    push_active = False
    
    # Smoothing variables
    prev_x, prev_y = 0, 0
    
    # Depth history for push
    z_history = collections.deque(maxlen=5)

    with mp_hands.Hands(
        max_num_hands=2,  # Allow 2 hands
        min_detection_confidence=0.7,
        min_tracking_confidence=0.7) as hands:

        while True:
            success, frame = cap.read()
            if not success: continue

            # Flip and format
            frame = cv2.flip(frame, 1)
            H, W, _ = frame.shape
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            results = hands.process(frame_rgb)

            # On-Screen Instructions (Simplified)
            cv2.rectangle(frame, (0,0), (W, 80), (0,0,0), -1)
            cv2.putText(frame, "Index: Move | Index+Thumb: Left Click | Mid+Thumb: Right Click", 
                        (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)
            cv2.putText(frame, "Ring+Thumb: Full Screenshot | Push: Air Click | 'q': Quit", 
                        (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)

            if results.multi_hand_landmarks:
                
                # Logic: Choose the "Left" hand (Screen Left)
                # We sort the enumerated list of hands by the x-coordinate of the Wrist (landmark 0).
                # Smaller x = Left side of screen.
                
                # Create a list of (index, landmarks)
                hands_list = list(enumerate(results.multi_hand_landmarks))
                
                # Sort by Wrist X (landmarks.landmark[0].x)
                hands_list.sort(key=lambda x: x[1].landmark[0].x)
                
                # The first one is the left-most hand
                primary_hand_idx, primary_hand_landmarks = hands_list[0]

                for idx, hand_landmarks in hands_list:
                    # Draw all hands
                    mp_drawing.draw_landmarks(frame, hand_landmarks, mp_hands.HAND_CONNECTIONS)
                    
                    # Only process gestures for the PRIMARY hand
                    if idx == primary_hand_idx:
                        # Mark it visually
                        wrist = hand_landmarks.landmark[0]
                        cv2.putText(frame, "PRIMARY", (int(wrist.x*W), int(wrist.y*H)+20), 
                                    cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

                        # --- Landmarks ---
                        thumb = hand_landmarks.landmark[4]
                        index = hand_landmarks.landmark[8]
                        middle = hand_landmarks.landmark[12]
                        ring = hand_landmarks.landmark[16]

                        # --- 1. Cursor Control (Index Finger) ---
                        # scale = (val - 0.5) * sensitivity + 0.5
                        x_cam = index.x
                        y_cam = index.y

                        x_mapped = (x_cam - 0.5) * MOUSE_SENSITIVITY + 0.5
                        y_mapped = (y_cam - 0.5) * MOUSE_SENSITIVITY + 0.5
                        x_mapped = max(0, min(1, x_mapped))
                        y_mapped = max(0, min(1, y_mapped))

                        raw_x = int(x_mapped * screen_width)
                        raw_y = int(y_mapped * screen_height)
                        
                        # Smoothing
                        cur_x = prev_x + (raw_x - prev_x) * SMOOTHING
                        cur_y = prev_y + (raw_y - prev_y) * SMOOTHING
                        
                        try:
                            pyautogui.moveTo(cur_x, cur_y)
                        except pyautogui.FailSafeException:
                            pass
                        
                        prev_x, prev_y = cur_x, cur_y

                        # --- 2. Left Click (Index + Thumb) ---
                        dist_left = calculate_distance(index, thumb)
                        if dist_left < CLICK_THRESHOLD:
                            cv2.circle(frame, (int(index.x*W), int(index.y*H)), 15, (0, 255, 0), cv2.FILLED)
                            if not left_click_active:
                                pyautogui.click()
                                print(">> LEFT CLICK triggered")
                                left_click_active = True
                        else:
                            left_click_active = False

                        # --- 3. Right Click (Middle + Thumb) ---
                        dist_right = calculate_distance(middle, thumb)
                        if dist_right < CLICK_THRESHOLD:
                            cv2.circle(frame, (int(middle.x*W), int(middle.y*H)), 15, (0, 0, 255), cv2.FILLED)
                            if not right_click_active:
                                pyautogui.rightClick()
                                print(">> RIGHT CLICK triggered")
                                right_click_active = True
                        else:
                            right_click_active = False

                        # --- 4. Screenshot (Ring + Thumb) ---
                        dist_ring = calculate_distance(ring, thumb)
                        screenshot_state = getattr(main, "screenshot_state", False)

                        if dist_ring < CLICK_THRESHOLD:
                            cv2.circle(frame, (int(ring.x*W), int(ring.y*H)), 15, (255, 255, 0), cv2.FILLED)
                            if not screenshot_state:
                                # Take WHOLE SCREENSHOT using MacOS native command
                                import subprocess
                                timestamp = int(time.time())
                                desktop_path = os.path.join(os.path.expanduser("~"), "Desktop")
                                filename = f"full_screenshot_{timestamp}.png"
                                filepath = os.path.join(desktop_path, filename)
                                
                                try:
                                    # native macOS screenshot command
                                    subprocess.run(["screencapture", "-x", filepath], check=True)
                                    print(f">> FULL SCREENSHOT SAVED: {filepath}")
                                    
                                    # Visual Feedback text
                                    cv2.putText(frame, "SCREENSHOT SAVED!", (W//2 - 150, H//2), 
                                                cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 0), 3)
                                except Exception as e:
                                    print(f"Error taking screenshot: {e}")
                                
                                screenshot_state = True
                                main.screenshot_state = True
                        else:
                             main.screenshot_state = False


                        # --- 5. Push Gesture (Z-Depth) ---
                        current_z = index.z
                        z_history.append(current_z)
                        avg_z = sum(z_history) / len(z_history)
                        
                        # Visual Z-bar
                        bar_h = int(abs(avg_z) * 500)
                        cv2.rectangle(frame, (W-30, H-bar_h), (W-10, H), (255, 0, 0), -1)
                        
                        if avg_z < PUSH_THRESHOLD:
                             if not push_active:
                                 # Only push-click if we aren't already pinching
                                 if not left_click_active and not right_click_active:
                                     pyautogui.click()
                                     print(f">> PUSH CLICK triggered (Z: {avg_z:.3f})")
                                     cv2.putText(frame, "PUSH!", (W//2, H//2), cv2.FONT_HERSHEY_SIMPLEX, 2, (0,255,255), 3)
                                     push_active = True
                        else:
                            if avg_z > (PUSH_THRESHOLD + PUSH_HYSTERESIS):
                                push_active = False

            cv2.imshow('Final AI Mouse', frame)
            if cv2.waitKey(5) & 0xFF == ord('q'):
                break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
