"""
Keyboard Controller - Support for key combinations
"""

import pyautogui
import time


class KeyboardController:
    def __init__(self):
        """Initialize keyboard controller"""
        pyautogui.PAUSE = 0.1
        pyautogui.FAILSAFE = False
        
    def press_key(self, key):
        """Press a single key"""
        try:
            pyautogui.press(key)
            print(f"⌨️  Pressed: {key}")
            return True
        except Exception as e:
            print(f"❌ Error: {e}")
            return False
    
    def press_hotkey(self, *keys):
        """Press a key combination (e.g., command, right)"""
        try:
            pyautogui.hotkey(*keys)
            print(f"⌨️  Pressed: {'+'.join(keys)}")
            return True
        except Exception as e:
            print(f"❌ Error: {e}")
            return False
    
    def send_gesture_command(self, command_type):
        """
        Send keyboard input based on command type.
        Can be a single key or a tuple for combinations.
        """
        try:
            if isinstance(command_type, tuple):
                # Key combination (e.g., ('command', 'right'))
                pyautogui.hotkey(*command_type)
                print(f"⌨️  {'+'.join(command_type)}")
            else:
                # Single key
                pyautogui.press(command_type)
                print(f"⌨️  {command_type}")
            return True
        except Exception as e:
            print(f"❌ Error: {e}")
            return False
