"""
Hand gesture detection with longer hold time
"""

import cv2
import mediapipe as mp
import math
import time


class HandGestureController:
    def __init__(self):
        self.mp_hands = mp.solutions.hands
        self.mp_drawing = mp.solutions.drawing_utils
        self.mp_drawing_styles = mp.solutions.drawing_styles
        
        self.hands = self.mp_hands.Hands(
            max_num_hands=1,
            min_detection_confidence=0.7,
            min_tracking_confidence=0.7
        )
        
        self._gesture_tracking = {}
        self._gesture_hold_duration = 0.4  # Increased to 0.4s

    def process(self, rgb_frame):
        """Process frame and detect gestures"""
        results = self.hands.process(rgb_frame)
        detected_results = []

        if not (results.multi_hand_landmarks and results.multi_handedness):
            return detected_results

        for idx, landmarks in enumerate(results.multi_hand_landmarks):
            handedness = results.multi_handedness[idx].classification[0].label
            lms = [(lm.x, lm.y, lm.z) for lm in landmarks.landmark]

            gesture = self._recognize_gesture(lms)
            stable_gesture = self._track_gesture_stability(gesture, handedness)
            
            if stable_gesture:
                detected_results.append({
                    'gesture': stable_gesture,
                    'hand': handedness,
                    'landmarks': landmarks
                })
            else:
                # Still add for drawing
                detected_results.append({
                    'gesture': None,
                    'hand': handedness,
                    'landmarks': landmarks
                })

        return detected_results

    def _recognize_gesture(self, lms):
        """Recognize gestures by counting extended fingers"""
        fingers = self._get_extended_fingers(lms)
        count = sum(fingers)
        
        if count == 0:
            return 'FIST'
        elif count == 1:
            return 'ONE'
        elif count == 2:
            return 'TWO'
        elif count == 3:
            return 'THREE'
        elif count == 4:
            return 'FOUR'
        elif count >= 5:
            return 'OPEN'
        
        return None

    def _get_extended_fingers(self, lms):
        """Check which fingers are extended"""
        fingers = []
        
        # Thumb
        thumb_tip = lms[4]
        thumb_ip = lms[3]
        thumb_extended = abs(thumb_tip[0] - lms[0][0]) > abs(thumb_ip[0] - lms[0][0])
        fingers.append(thumb_extended)
        
        # Other fingers
        finger_tips = [8, 12, 16, 20]
        finger_pips = [6, 10, 14, 18]
        
        for tip_idx, pip_idx in zip(finger_tips, finger_pips):
            tip_y = lms[tip_idx][1]
            pip_y = lms[pip_idx][1]
            fingers.append(tip_y < pip_y - 0.05)
        
        return fingers
    
    def _track_gesture_stability(self, gesture, hand_id):
        """Track gesture over time for stability"""
        current_time = time.time()
        
        if gesture is None:
            if hand_id in self._gesture_tracking:
                del self._gesture_tracking[hand_id]
            return None
        
        if hand_id not in self._gesture_tracking:
            self._gesture_tracking[hand_id] = {
                'gesture': gesture,
                'start_time': current_time
            }
            return None
        
        tracking = self._gesture_tracking[hand_id]
        
        if tracking['gesture'] != gesture:
            self._gesture_tracking[hand_id] = {
                'gesture': gesture,
                'start_time': current_time
            }
            return None
        
        if current_time - tracking['start_time'] >= self._gesture_hold_duration:
            return gesture
        
        return None
    
    def reset_gesture_tracking(self, hand_id=None):
        """Reset tracking"""
        if hand_id is None:
            self._gesture_tracking.clear()
        elif hand_id in self._gesture_tracking:
            del self._gesture_tracking[hand_id]

    def draw(self, frame, landmarks):
        """Draw hand landmarks"""
        self.mp_drawing.draw_landmarks(
            frame,
            landmarks,
            self.mp_hands.HAND_CONNECTIONS,
            self.mp_drawing_styles.get_default_hand_landmarks_style(),
            self.mp_drawing_styles.get_default_hand_connections_style()
        )
