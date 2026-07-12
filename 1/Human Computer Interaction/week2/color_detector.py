import cv2
import numpy as np

def main():
    # 1. Open Camera
    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        print("Error: Could not open camera.")
        return

    print("Press 'q' to quit.")

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Can't receive frame (stream end?). Exiting ...")
            break

        # 2. Convert to HSV
        # OpenCV H: 0-179, S: 0-255, V: 0-255
        hsv_img = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

        # 3. Create Mask based on Skin Model
        # Rules (Normalized 0-1):
        # H <= 0.1 OR H >= 0.9
        # 0.2 <= S <= 0.6
        # V >= 0.4
        
        # Scale to OpenCV ranges:
        # H: 0-179. 0.1 -> 18. 0.9 -> 162.
        # S: 0-255. 0.2 -> 51. 0.6 -> 153.
        # V: 0-255. 0.4 -> 102.

        H = hsv_img[:, :, 0]
        S = hsv_img[:, :, 1]
        V = hsv_img[:, :, 2]

        # Mask specific conditions
        # H condition: (H <= 18) OR (H >= 162)
        mask_h = (H <= 18) | (H >= 162)
        
        # S condition: 51 <= S <= 153
        mask_s = (S >= 51) & (S <= 153)
        
        # V condition: V >= 102
        mask_v = (V >= 102)

        # Combine
        skin_mask = mask_h & mask_s & mask_v
        skin_mask = skin_mask.astype(np.uint8) * 255

        # 4. Post-processing (Scaling and Median Filter)
        
        # Scale down and back up (Using 15%)
        scale_factor = 0.15
        height, width = skin_mask.shape
        new_height, new_width = int(height * scale_factor), int(width * scale_factor)
        
        # Resize down (Nearest Neighbor)
        mask_small = cv2.resize(skin_mask, (new_width, new_height), interpolation=cv2.INTER_NEAREST)
        
        # Resize up (Nearest Neighbor)
        mask_scaled = cv2.resize(mask_small, (width, height), interpolation=cv2.INTER_NEAREST)
        
        # Median Filter
        # Kernel size usually odd, let's pick 5
        mask_final = cv2.medianBlur(mask_scaled, 5)

        # 5. Visualization
        cv2.imshow('Original Camera', frame)
        cv2.imshow('Processed Mask', mask_final)

        # Quit on 'q'
        if cv2.waitKey(1) == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main()
