# week5: Image Quality, Compressions, & Format Analysis

## 🎯 Task Objective
Perform compression, conversion, and resolution scaling experiments on three types of digital assets:
1.  **Bunny:** Photographic image (continuous tones, complex gradients).
2.  **Declaration of Independence:** Scanned text document (high contrast, distinct edges).
3.  **London Transportation Map:** Schema/diagram (sharp vector-like lines, flat colors).

---

## 🗂️ Experiment Steps

### 1. Format Conversion (JPEG vs. PNG)
*   Convert each of the images from JPEG to PNG and vice versa.
*   Verify the outcome by documenting:
    *   File size changes.
    *   Visual quality changes (especially sharp borders/text in the London Map).

### 2. Quality Factor (QF) Compression Analysis
*   With all three images in JPEG format, export them using different Quality Factors (if numerical output is supported, use: `100`, `75`, `50`, `25`, and `1`).
*   Document:
    *   Resulting file sizes.
    *   Visual quality degradation after compression.
    *   Provide your evaluation on whether the compressed image remains acceptable for web use at each level.

### 3. Resolution Reduction
*   Scale down the resolution of each original image by **half** (reduce width & height by 2).
*   Repeat the Quality Factor analysis from Step 2 with these lower-resolution images.
*   Document file sizes and visual accessibility/readability.

---

## 📋 Deliverables & Submission
*   Compile all findings, file sizes, visual assessments, and comparisons into a single PDF report.
*   Name the report `dd-week5.pdf`.
*   Submit the report via MS Teams chat.

---

## 💡 Notes & Tooling Suggestions
*   **Recommended Applications:** You can use [IrfanView](https://www.irfanview.com) (Windows), [GIMP](https://www.gimp.org) (Cross-platform), Photoshop, or any command-line image tool (like `imagemagick`) to handle the bulk conversion and scaling.
*   **Vector vs. Raster:** Compare rasterized output with the vector-original SVG versions of the London transportation map to highlight quality/scaling benefits.
*   **Browser Compatibility Warning:** While newer compression formats like HEIC provide exceptional quality-to-size ratios, they require Apple licensing and are currently only supported natively on Safari ([caniuse HEIC](https://caniuse.com/?search=heic)). For general web applications, stick to standardized formats like JPG, PNG, WebP, and AVIF.
