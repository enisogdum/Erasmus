# week10: Node.js, npm Project Initialization, & Express.js Basics

## 🎯 Task Objective
Learn the fundamentals of JavaScript-based web development environments (part of the MEAN stack). This task is divided into two parts: initializing a basic project and managing dependencies with `npm`, and scaffolding an Express server using the `express-generator`.

---

## 🗂️ Project Stages

### 📖 Stage 0: Environment Preparation
*   Review the Node.js Getting Started guides: [Node.js Learn Guide](https://nodejs.org/en/learn/).
*   Verify your local Node.js and npm installations:
    ```bash
    node -v
    npm -v
    ```

### 📂 Stage 1: Basic Project & npm Management
1.  **Directory Initialization:** Create a new folder named `new` and initialize it as an npm project:
    ```bash
    mkdir new
    cd new
    npm init -y
    ```
2.  **Add Files:** Move or create your static HTML and asset files inside a folder (e.g., `public`).
3.  **Install a HTTP Server:** Install a lightweight static server package:
    ```bash
    npm install serve
    # OR
    npm install http-server
    ```
4.  **Configure Scripts:** Edit the generated `package.json` to include a start script pointing to your server and port (e.g., custom port $\ge 1024$ to avoid admin restrictions):
    ```json
    "scripts": {
      "start": "serve public -l 5000"
    }
    ```
5.  **Run Server:** Execute `npm start` and verify that the page is accessible at the local address (e.g., `http://localhost:5000`).

---

### 🚀 Stage 2: Elementary Express Project
1.  **Express Scaffolding:** Scaffold a separate Express project using the official CLI generator. Set the view rendering engine to **Twig**:
    ```bash
    npx express-generator --view=twig express-app
    ```
    *References:* [Express Starter Guide](https://expressjs.org/en/starter/) and [Express Application Generator](https://expressjs.org/en/starter/generator.html)
2.  **Run & Launch:**
    *   Navigate into the generated app directory: `cd express-app`
    *   Install dependencies: `npm install`
    *   Start the server: `DEBUG=express-app:* npm start` (or standard `npm start`)
3.  **Verification:** Verify that the Express landing page renders correctly in your browser (usually on port `3000`).

---

## 📋 Deliverables & Submission Checklist
Provide a comprehensive report containing:
- [ ] Command lines used in the terminal for installation, project setup, and runs.
- [ ] Contents of important created config files (e.g., `package.json`).
- [ ] **2 screenshots:**
    1.  Browser showing your static page running through the `serve`/`http-server` script on port 5000.
    2.  Browser showing the working Express.js welcome page on port 3000.
