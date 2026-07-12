# week11: Express.js Routing & Dynamic Server-Side Templating (Twig)

## 🎯 Task Objective
Implement server-side routing and dynamic HTML page rendering using Express.js and the Twig template engine. Create three distinct URL endpoints that accept query parameters (GET or POST) and render dynamic templates containing conditional logic and loops.

---

## 📖 Study & References
*   **Express Routing:** Read the [Express Routing Guide](https://expressjs.org/en/guide/routing.html) to understand routes, path parameters, and query strings.
*   **Express Application Generator:** Reference [Express Generator](https://expressjs.org/en/starter/generator.html) to scaffold projects.
*   **Twig Reference:** Twig is a template engine for JavaScript similar to PHP's Twig. Check [Twig Documentation](https://twig.symfony.com) for syntax guidelines (conditionals, loops, etc.).

---

## 🗂️ Project Tasks

### 1. Project Setup
Generate an Express application with Twig as the view engine:
```bash
npx express-generator --view=twig routing-app
cd routing-app
npm install
```

### 2. URL Routes & Dynamic Pages
Configure the Express application routes to map incoming HTTP requests (suggested GET parameters, e.g., `?param=value`) to three dynamic pages:

#### 🔹 Page 1: Conditional Text Matching
*   **URL:** `/page1`
*   **Behavior:** Reads a query parameter (e.g., `?val=A`). 
*   **Logic:** Evaluates the parameter in the route or Twig template. Detects values `A`, `B`, and `C` and displays custom text messages tailored to each of these inputs.

#### 🔹 Page 2: Numeric Evaluation
*   **URL:** `/page2`
*   **Behavior:** Reads a numeric query parameter (e.g., `?num=42`).
*   **Logic:** Detects whether the input number is positive, negative, or exactly zero, and displays the corresponding classification message.

#### 🔹 Page 3: Dynamic List Generator
*   **URL:** `/page3`
*   **Behavior:** Reads a parameter containing list items (e.g., a comma-separated list like `?items=Apples,Bananas,Oranges` or separate parameters).
*   **Logic:** Parses the list into an array (e.g., via `.split(',')` in Node.js) and loops through the array inside the Twig template (`{% for item in items %}`) to render a structured bulleted HTML list.

---

## 📋 Deliverables & Submission
Prepare a submission containing:
- [ ] Source code of the main application routing file (e.g., `app.js` or `routes/index.js` where routing is handled).
- [ ] The Twig template files used to render the HTML pages.
- [ ] **3 Screenshots:** A screenshot of each page running in the browser, showing the browser URL bar (with the parameters) and the resulting HTML content.
