# LayoutIt Generator Instructions

These instructions guide you through recreating the 3 layouts using the [LayoutIt Grid Generator](https://grid.layoutit.com/).

## Layout 1: Holy Grail Layout

1.  **Columns**:
    *   Set the grid to have **3 columns**.
    *   Set the first column to `200px` (or `20%`).
    *   Set the middle column to `1fr` (auto).
    *   Set the last column to `200px` (or `20%`).
2.  **Rows**:
    *   Set the grid to have **3 rows**.
    *   Set the first row to `auto` (or `100px`).
    *   Set the middle row to `1fr`.
    *   Set the last row to `auto` (or `80px`).
3.  **Gap**:
    *   Set **Row Gap** and **Column Gap** to `10px`.
4.  **Areas**:
    *   Select the top 3 cells and name the area `header`.
    *   Select the bottom 3 cells and name the area `footer`.
    *   Select the middle-left cell and name it `nav`.
    *   Select the middle-center cell and name it `main`.
    *   Select the middle-right cell and name it `aside`.
5.  **Get Code**:
    *   Click "Get Code" to copy the generated CSS and HTML.

## Layout 2: Dashboard Layout

1.  **Columns**:
    *   Set the grid to have **4 columns**.
    *   Set all columns to `1fr`.
2.  **Rows**:
    *   Set the grid to have **4 rows**.
    *   Row 1: `100px`
    *   Row 2: `200px`
    *   Row 3: `200px`
    *   Row 4: `100px`
3.  **Gap**:
    *   Set gaps to `15px`.
4.  **Areas (or Placement)**:
    *   **Header**: Select the entire top row (4 cells).
    *   **Sidebar**: Select the first cell of Row 2 and Row 3 (drag to combine vertically).
    *   **Content 1**: Select the middle two cells of Row 2.
    *   **Content 2**: Select the last cell of Row 2.
    *   **Content 3**: Select the last 3 cells of Row 3 (spanning columns 2, 3, and 4).
    *   **Footer**: Select the entire bottom row.

## Layout 3: Masonry / Gallery

*Note: LayoutIt is great for explicit grids, but "Masonry" often relies on `auto-fill` and `dense` packing which might be harder to visualize perfectly in a static generator without custom code. However, you can set up the base structure.*

1.  **Columns**:
    *   Set the grid to have **4 columns** (all `1fr`).
2.  **Rows**:
    *   Set the grid to have **4 rows** (all `200px`).
3.  **Gap**:
    *   Set gaps to `15px`.
4.  **Placement**:
    *   Leave most cells as single units (1x1).
    *   Select a few cells and combine them to make "Wide" items (2x1).
    *   Select a few cells and combine them to make "Tall" items (1x2).
    *   Select one block to be "Big" (2x2).
5.  **Code Adjustment**:
    *   After generating, you will likely need to change `grid-template-columns` to `repeat(auto-fill, minmax(250px, 1fr))` in your CSS to make it responsive like the homework example.
