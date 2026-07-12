"""
Music App Gesture Control
"""

import cv2
from gesture_controller import GestureController


def main():
    controller = GestureController()

    # Open webcam
    cap = cv2.VideoCapture(0)
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

    print("=" * 50)
    print("🎵 MUSIC APP GESTURE CONTROL")
    print("=" * 50)
    print("Gestures (hold for 0.4 seconds):")
    print("  FIST (0 fingers)    → Pause (Space)")
    print("  1 FINGER            → Next Track (⌘→)")
    print("  2 FINGERS           → Previous Track (⌘←)")
    print("  3 FINGERS           → Volume Up (⌘↑)")
    print("  OPEN HAND (5)       → Volume Down (⌘↓)")
    print("")
    print("⏱️  Wait 1.5 seconds between gestures")
    print("⚠️  Make sure Music app is open!")
    print("❌ Press 'q' to quit")
    print("=" * 50)

    while True:
        ret, frame = cap.read()
        if not ret:
            continue

        # Mirror
        frame = cv2.flip(frame, 1)

        # Process and draw
        processed_frame, _ = controller.process_frame(frame)

        # Show
        cv2.imshow("Music Gesture Control", processed_frame)

        if cv2.waitKey(1) & 0xFF == ord("q"):
            break

    cap.release()
    controller.cleanup()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
