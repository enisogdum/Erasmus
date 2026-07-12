# week17: Angular + Express Hybrid Application

This project consists of an Angular frontend served by an Express.js backend.

## 🚀 Quick Start (Production Mode)

The easiest way to run the full application (Frontend + Backend) is via the Express server:

1.  Navigate to the backend directory:
    ```bash
    cd "week 17/backend"
    ```
2.  Start the server:
    ```bash
    node server.js
    ```
3.  Open your browser to:
    *   **http://localhost:3000**

## 💻 Development Mode

If you want to modify the Angular code and see changes in real-time:

1.  Navigate to the frontend directory:
    ```bash
    cd "week 17/frontend"
    ```
2.  Run the Angular development server (using `npx`):
    ```bash
    npx ng serve --open --port 4201
    ```
    *   *Note: Using port 4201 to avoid conflicts.*
    *   *Note: Use `npx` because Angular CLI is not installed globally.*

3.  Access the frontend at:
    *   **http://localhost:4201**

## 🔄 Re-building for Production

If you make changes to the Angular frontend and want to see them in the Production Mode (http://localhost:3000), you must rebuild:

```bash
cd "week 17/frontend"
npx ng build --configuration production
```
Then restart the backend server.
