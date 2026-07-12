# week2: Basic HTML Styling (Inline, Internal, & External CSS)

## 🎯 Task Objective
Prepare a basic styling for the selected table created during the week1 exercise, transitioning from inline styles to structured internal or external CSS rules.

---

## 🗂️ Project Stages

### 📖 Stage 0: Study CSS Basics
*   Read the MDN article: [Styling Basics](https://developer.mozilla.org/en-US/docs/Learn_web_development/Core/Styling_basics) to understand the foundational concepts of styling HTML elements.

### 🎨 Stage 1: Inline Styling
*   Modify the visual properties of **every cell** in the table (change at least **1 property per cell**).
*   Focus **strictly on visual properties**:
    *   Fonts (size, style, weight, family)
    *   Decorations (underline, line-through, etc.)
    *   Colors and Backgrounds
*   *Note: Do not modify layout sizing (e.g., width, height, margins, padding) in this step; keep sizing rules for future exercises.*

### 🛠️ Stage 2: Convert to Structured CSS (Internal/External)
If you used inline styling, convert it entirely into internal (`<style>` tag) or external (`.css` stylesheet) styles. Ensure you:
1.  **Identify Common Styles:** Define reusable classes/rules for repeated patterns instead of styling each element individually.
2.  **Verify Visual Consistency:** Confirm that the internal/external formatting matches the appearance of your inline formatting.
3.  **Avoid ID Over-usage:** Avoid simply extracting inline styles into a series of ID-based selectors (e.g., `#cell1`, `#cell2`). Instead, build a clean class hierarchy and leverage descendant/element selectors.
4.  **Consolidate Rules:** If you have mixed formatting styles, transform all of them into a single, cohesive form (either entirely internal or entirely external).

---

## 📋 Deliverables & Submission
Prepare and submit the following:
- [ ] All source files (HTML, and CSS if external styling is used).
- [ ] A **single PDF report** containing formatting comparisons:
    *   **Original Table:** Screenshot & code.
    *   **Inline Version (Stage 1):** Screenshot & code.
    *   **Final Styled Version (Stage 2):** Screenshot & code.

---

## 📚 Study References & Guidelines
*   **CSS Structure:** [How CSS is structured (MDN)](https://developer.mozilla.org/en-US/docs/Learn/CSS/First_steps/How_CSS_is_structured)
*   **Styling Text:** [Styling text in CSS (MDN)](https://developer.mozilla.org/en-US/docs/Learn/CSS/Styling_text)
*   **CSS Reference:** [MDN CSS Reference Guide](https://developer.mozilla.org/en-US/docs/Web/CSS/Reference)
*   **HTMLElement.style:** [MDN HTMLElement Style API](https://developer.mozilla.org/en-US/docs/Web/API/HTMLElement/style)
