import cv2
import mediapipe as mp

def main():
    # Initialize MediaPipe Hands
    mp_hands = mp.solutions.hands
    mp_drawing = mp.solutions.drawing_utils
    mp_drawing_styles = mp.solutions.drawing_styles

    # Setup Hands model
    # min_detection_confidence: Minimum confidence value ([0.0, 1.0]) for hand detection to be considered successful.
    # min_tracking_confidence: Minimum confidence value ([0.0, 1.0]) for the hand landmarks to be considered tracked successfully.
    with mp_hands.Hands(
        model_complexity=0,
        min_detection_confidence=0.5,
        min_tracking_confidence=0.5) as hands:

        cap = cv2.VideoCapture(0)
        if not cap.isOpened():
            print("Cannot open camera")
            return

        print("Press 'q' to quit.")

        while True:
            ret, frame = cap.read()
            if not ret:
                print("Ignoring empty camera frame.")
                continue

            # Flip the image horizontally for a later selfie-view display
            frame = cv2.flip(frame, 1)

            # To improve performance, optionally mark the image as not writeable to pass by reference.
            frame.flags.writeable = False
            # Convert BGR to RGB
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            
            # Process the image and find hands
            results = hands.process(frame_rgb)

            # Draw the hand annotations on the image.
            frame.flags.writeable = True
            # We don't need to convert back to BGR for drawing, but for imshow we usually stick to BGR.
            # (Note: frame is still BGR because we only converted a copy to RGB or used flags, 
            # actually cvtColor returns a new image, so 'frame' is still BGR)
            
            if results.multi_hand_landmarks:
                for hand_landmarks in results.multi_hand_landmarks:
                    mp_drawing.draw_landmarks(
                        frame,
                        hand_landmarks,
                        mp_hands.HAND_CONNECTIONS,
                        mp_drawing_styles.get_default_hand_landmarks_style(),
                        mp_drawing_styles.get_default_hand_connections_style())

            cv2.imshow('MediaPipe Hands', frame)

            if cv2.waitKey(5) & 0xFF == ord('q'):
                break

        cap.release()
        cv2.destroyAllWindows()

if __name__ == '__main__':
    main()
