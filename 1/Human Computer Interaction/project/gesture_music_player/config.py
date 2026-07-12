"""
Configuration - Longer cooldown to prevent confusion
"""

# MediaPipe Settings
MEDIAPIPE_CONFIG = {
    'max_num_hands': 1,
    'min_detection_confidence': 0.7,
    'min_tracking_confidence': 0.7
}

# Gesture Settings - LONGER cooldown
GESTURE_CONFIG = {
    'gesture_hold_time': 0.4,     # Hold gesture 0.4s
    'cooldown_time': 1.5,         # Wait 1.5s between gestures (was 0.6s)
}

# Camera Settings
UI_CONFIG = {
    'camera_width': 640,
    'camera_height': 480,
}

# 5 GESTURES for Music App
GESTURE_COMMANDS = {
    'FIST': 'space',                    # 0 fingers → Pause (Space)
    'ONE': ('command', 'right'),        # 1 finger → Next Track (⌘►)
    'TWO': ('command', 'left'),         # 2 fingers → Previous Track (⌘◄)
    'THREE': ('command', 'up'),         # 3 fingers → Volume Up (⌘▲)
    'OPEN': ('command', 'down'),        # 5 fingers → Volume Down (⌘▼)
}
