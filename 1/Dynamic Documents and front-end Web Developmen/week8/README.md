# week8: CSS Grid Layouts & Webpage Responsiveness Analysis

## 🎯 Task Objective
This week's task is divided into two parts: recreating common grid-based layouts and testing/analyzing card layouts under different resizing behaviors (Fixed, Fluid, Responsive, Adaptive).

---

## 🗂️ Part 1: CSS Grid Layout Recreation

### 1. Study Guidelines
*   **CSS Grid:** [MDN CSS Grid Layout](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_grid_layout)
*   **Grid Guide:** [MDN CSS Layout: Grids](https://developer.mozilla.org/en-US/docs/Learn/CSS/CSS_layout/Grids)

### 2. Implementation Steps
*   Select **3 distinct layouts** presented during the "Web design principles" lecture.
*   Recreate their structural grids using an online CSS Grid Generator:
    *   [LayoutIt Grid (Preferred)](https://grid.layoutit.com)
    *   [Bradwoods Grid Customizer](https://layout.bradwoods.io/customize)
    *   [CSS Grid Generator](https://cssgrid-generator.netlify.app)
*   Populate the grid cells with placeholder content and images indicating their intended use (e.g., Header, Sidebar, Main Content, Footer, Cards).
*   Test and refine the grid structure (you can use platforms like [CodePen](https://codepen.io/) for sandbox testing).
*   Collect the HTML and CSS code, and capture screenshots of each completed layout design.

*Note: For 1D layouts or nested alignments, remember that Flexbox is generally preferred over Grid. Refer to [MDN Flexbox Layout](https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_flexible_box_layout).*

---

## 🗂️ Part 2: Card Layout Responsiveness Comparison

### 1. Setup
*   Obtain the provided webpage archive containing the card layout examples (using internal CSS).
*   Unpack and open the page in a desktop browser (Chrome recommended). Open DevTools (`F12`).

### 2. Viewport Resizing & Measurement
*   **Initial Viewport (Wide):** Capture a screenshot of the entire page layout at $\ge 1200\text{px}$ width.
*   **Narrowing Viewport:** Gradually scale down the viewport width to **1024px**, **800px**, **600px**, and **400px**.
    *   Document the visual changes for each card variant (**Fixed, Fluid, Responsive, Adaptive**).
    *   Capture a screenshot at each specified width.
*   **Element Dimensions:** At **1024px** and **400px** viewport widths, inspect the elements using DevTools and record the rendered widths (in pixels) for:
    *   Card outer border.
    *   Hero image.
    *   CTA button.
*   **Bug Hunt:** Note any layout breaking points, overflowing elements, clipping text, or other visual flaws.

---

## 📋 Deliverables & Submission
Combine the reports for both parts into a single submission (`dd-week8.zip` or a unified PDF file) containing:
- [ ] **Part 1 Grid Layouts:** HTML/CSS code files and screenshots of all 3 layouts.
- [ ] **Part 2 Analysis:**
    *   Screenshots at the required widths ($1200\text{px}$, $1024\text{px}$, $800\text{px}$, $600\text{px}$, $400\text{px}$).
    *   A table/section listing element dimensions for all cards at $1024\text{px}$ and $400\text{px}$.
    *   Detailed design descriptions of how each card responds to scaling.
    *   A conclusion (2–3 sentences) summarizing which card layout style handled the resizing best and why.
