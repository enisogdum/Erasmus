# week12: AI-Assisted Web Design & Generated Code Quality Analysis

## 🎯 Task Objective
Investigate AI-based design tools and layout generators (e.g., Framer AI and Copilot/general-purpose LLMs) to generate web layouts. Compare and evaluate the quality, cleanliness, semantic organization, and extensibility of AI-generated HTML/CSS code.

---

## 🗂️ Project Tasks

### 1. Setup
*   Access [Framer](https://framer.com) (you can use a temporary email address via `temp-mail.org` to set up an account if needed).
*   Create a new design layout project.

### 2. Framer AI Generation
Run generative design workflows inside Framer by navigating to **Main Menu (Framer Logo)** -> **Quick actions** -> **Generate Page**. Test **3 prompts** for each of the following **4 scenarios**:
1.  **Personal Webpage:** Emphasize profession, portfolio links, experience highlights, and background.
2.  **Webshop:** Provide details about specific item ranges (e.g., handcrafted items, boutique clothing, organic tea).
3.  **Corporate Website:** Select a standard business vertical (e.g., tech consulting, construction, solar energy).
4.  **Reference Mockup:** Recreate the website concept chosen for your Figma design in week6.

*Reference:* Explore [Framer Prompts Directory](https://prompts.framer.ai) to study successful layout prompts. Readjust prompts iteratively based on the initial output quality to fine-tune section distributions and color themes.

### 3. General Gen-AI Comparison
Replicate the same prompts with a general-purpose text/code LLM (e.g., ChatGPT, Copilot, Gemini) to generate full HTML and CSS files for the same four scenarios.

### 4. Code Quality & Formatting Review
Analyze the source code of the best outputs from both tools. Specifically inspect:
*   **Semantic Structure:** Use of modern tags (`<header>`, `<main>`, `<section>`, `<footer>`) versus nested `<div>` wrappers.
*   **CSS Style Organization:** Class naming conventions, responsiveness rules, readability, and redundant properties.
*   **Extensibility:** Ease of updating the layout, editing styles, or wrapping content.
*   *Reference Tooling:* Explore [HTML-ESLint](https://html-eslint.org/) (playground/rules) to see how static analysis rules identify bad patterns (e.g., duplicate IDs, unclosed tags, non-semantic blocks) in web layouts.

---

## 📋 Deliverables & Submission
Prepare a structured report containing:
- [ ] List of the exact prompts used for each tool and scenario.
- [ ] Screenshots of the generated Framer layouts.
- [ ] Screenshots of the alternative Gen-AI layouts.
- [ ] **Comparative Analysis:** Selection of the best visual result for each of the 4 scenarios.
- [ ] **Visual Review:** A critique of the designs (likes, dislikes, layout logic).
- [ ] **Code Quality Review:** A critical evaluation of the generated HTML/CSS files, focusing on clean structure, separation of concerns, and ease of maintenance.
