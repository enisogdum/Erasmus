# week14: Full-Stack Integration (Angular SPA Front-End & Express.js Back-End)

## 🎯 Task Objective
Connect the front-end (Angular SPA) with the back-end (Express.js server) to create a hybrid web system. Implement a password submission feature where the Angular client makes an asynchronous HTTP POST request containing a password, and the Express server responds with a verification confirmation.

---

## 🗂️ Project Stages

### 🔹 Stage 1: Express.js Backend Setup & Testing
1.  **Configure Express Endpoint:** Create a single API endpoint (e.g., `POST /api/verify`) that receives a JSON payload containing a password (e.g., `{"password": "XYZ"}`) and returns a confirmation string:
    *   *Example Response:* `{"message": "Password XYZ was retrieved"}`.
2.  **Enable CORS:** Since the Angular dev server runs on a different port (usually `4200` or `4201`) than the Express server (usually `3000`), install and configure the `cors` middleware in Express to allow cross-origin requests.
    *   *Reference:* [Express CORS Middleware](https://expressjs.com/en/resources/middleware/cors.html)
3.  **Standalone API Verification:** Test the endpoint using command-line tools or API clients:
    *   **CLI:** `curl` (macOS/Linux) or `Invoke-RestMethod` (Windows PowerShell).
    *   **Desktop Clients:** [Postman](https://www.postman.com), [Bruno](https://www.usebruno.com), or [Advanced REST Client](https://install.advancedrestclient.com).

---

### 🔹 Stage 2: Angular Frontend Setup & HTTP Client
1.  **HTTP Client Import:** Import `provideHttpClient()` (or `HttpClientModule` depending on your Angular version) in your application configuration or module file.
    *   *Reference:* [Angular HttpClient Guide](https://angular.dev/guide/http)
2.  **UI Components:**
    *   Add a password input field.
    *   Add a **Submit** button.
    *   Add a text label/container to display the dynamic feedback message returned from the server.
3.  **Request Handling:** Configure a service or component method to execute a POST request containing the password JSON to the Express server URL, subscribing to the response to bind the return message to the UI label.

---

### 🔹 Stage 3: Hybrid Production Deployment (Static Hosting)
1.  **Angular Build:** Compile production-ready static assets from your Angular project:
    ```bash
    ng build
    # or
    npm run build
    ```
2.  **Express Static Hosting:** Configure Express to serve these compiled static files (HTML, CSS, JS) from a directory (e.g., `public` or `dist`).
    *   *Reference:* [Serving static files in Express](https://expressjs.org/en/starter/static-files.html)
3.  **Consolidation:** Move the output folder from your Angular build (`dist/<project-name>/browser`) into the Express static directory. Start the Express server and verify that the Angular application loads and communicates correctly on a single port (no longer requiring CORS in production).

---

## 📋 Deliverables & Submission Checklist
Prepare a report containing:
- [ ] Source code of all files created or modified in the Angular project.
- [ ] Source code of all files created or modified in the Express.js project.
- [ ] **Screenshots:**
    *   API verification showing the successful JSON request and response via `curl` or Postman/Bruno.
    *   Angular app state **before** sending the request (clicking the submit button).
    *   Angular app state **after** receiving the server confirmation message.
