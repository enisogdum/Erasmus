const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = 3000;

// Middleware
app.use(cors()); // Enable CORS for development
app.use(express.json()); // Parse JSON bodies

// API Endpoint
app.post('/api/check-password', (req, res) => {
    const { password } = req.body;

    if (!password) {
        return res.status(400).json({ message: "No password provided" });
    }

    console.log(`Received password check request for: ${password}`);

    // Send confirmation response
    res.json({
        message: `Password "${password}" was retrieved`
    });
});

// Serve Angular static files (Production)
// Pointing to the specific build output directory
const angularBuildPath = path.join(__dirname, '../frontend/dist/password-strength-app/browser');
app.use(express.static(angularBuildPath));

// Fallback to index.html for Angular routing
app.get(/^(?!\/api).+/, (req, res) => {
    // Check if index.html exists before trying to send it
    const indexPath = path.join(angularBuildPath, 'index.html');
    res.sendFile(indexPath, (err) => {
        if (err) {
            res.status(404).send("Angular application not built yet. Run 'ng build' in frontend directory.");
        }
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
