"""
Gesture controller with longer cooldown
"""

import cv2
import time
from config import GESTURE_COMMANDS, GESTURE_CONFIG
from keyboard_controller import KeyboardController
from hand_controller import HandGestureController


class GestureController:
    def __init__(self):
        self.hand_controller = HandGestureController()
        self.keyboard = KeyboardController()
        
        self.last_any_command_time = 0
        self.cooldown = GESTURE_CONFIG['cooldown_time']  # Use from config (1.5s)
        
        print(f"✅ Gesture Controller initialized (cooldown: {self.cooldown}s)")

    def process_frame(self, frame):
        """Process frame with hand visualization"""
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        
        # Detect gestures
        results = self.hand_controller.process(rgb_frame)

        for res in results:
            landmarks = res['landmarks']
            gesture = res.get('gesture')
            hand_label = res['hand']
            
            # Draw hand landmarks
            self.hand_controller.draw(frame, landmarks)

            # Execute gesture command if detected
            if gesture:
                command = GESTURE_COMMANDS.get(gesture)
                if command:
                    success = self._execute_command(command, gesture)
                    if success:
                        self.hand_controller.reset_gesture_tracking(hand_label)

        return frame, results

    def _execute_command(self, command, gesture):
        """Execute keyboard command with longer cooldown"""
        now = time.time()
        
        # Check cooldown
        time_since_last = now - self.last_any_command_time
        if time_since_last < self.cooldown:
            return False
        
        try:
            success = self.keyboard.send_gesture_command(command)
            if success:
                self.last_any_command_time = now
                print(f"→ {gesture} (wait {self.cooldown}s)")
                return True
        except Exception as e:
            print(f"Error: {e}")
        
        return False

    def cleanup(self):
        self.hand_controller.hands.close()
