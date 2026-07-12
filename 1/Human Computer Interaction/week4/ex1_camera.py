import cv2

# Initialize the camera
cap = cv2.VideoCapture(0) # Camera number (0 for the default camera)

if not cap.isOpened():
    print("Cannot open camera")
    exit()

print("Press 'q' to quit.")

while True:
    ret, frame = cap.read()
    if not ret:
        print("Can't receive frame (stream end?). Exiting ...")
        continue

    # Mirror the image horizontally
    frame = cv2.flip(frame, 1)

    # Display the image
    cv2.imshow("Camera image", frame)

    # Exit the program when the 'q' key is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
