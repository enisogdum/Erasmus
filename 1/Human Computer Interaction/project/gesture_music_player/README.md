# 🎵 Music App Gesture Control

Control Apple Music with simple hand gestures!

## 🎯 5 Simple Gestures

| Gesture | Fingers | Music Control | Shortcut |
|---------|---------|---------------|----------|
| **FIST** | 0 | Pause/Play | Space |
| **ONE** | 1 | Next Track | ⌘→ |
| **TWO** | 2 | Previous Track | ⌘← |
| **THREE** | 3 | Volume Up | ⌘↑ |
| **OPEN** | 5 | Volume Down | ⌘↓ |

## 🚀 Quick Start

```bash
python main.py
```

## 🎮 How to Use

1. Open **Music app**
2. Run: `python main.py`
3. Show hand gestures (hold 0.3 seconds)
4. Music controls work automatically!
5. Press 'q' to quit

## 💡 Tips

- Keep hand clearly visible
- Hold gestures steady for 0.3 seconds
- Good lighting improves detection
- Green lines show hand tracking

## ⚙️ Technical

- **Tracking**: MediaPipe Hands
- **Detection**: Finger counting (0-5)
- **Hold Time**: 0.3 seconds
- **Cooldown**: 0.6 seconds

---

**Made with MediaPipe and OpenCV**
