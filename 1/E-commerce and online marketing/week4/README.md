# week4: Local Server Setup & WooCommerce Installation

## Task Overview
Prepare your local development environment for the forthcoming e-commerce tasks. You will install a web & database server stack (XAMPP) and configure a self-hosted e-shop platform (WordPress + WooCommerce).

---

## Step I: XAMPP Server Installation

1.  **📥 Download & Install:**
    *   Download and install [XAMPP](https://www.apachefriends.org).
    *   *Note:* If using an RDP session, use the portable version and update the Apache port in `xampp\apache\conf\httpd.conf` from `80` to a number above `1024`.
2.  **📖 Read Documentation:**
    *   Review the manual and documentation. Use this [Ionos XAMPP Tutorial](https://www.ionos.com/digitalguide/server/tools/xampp-tutorial-create-your-own-local-test-server/) as an installation guideline.
3.  **🖥️ Check Server Status:**
    *   Launch the XAMPP Control Panel/Monitoring tool and ensure Apache and MySQL are running successfully.
4.  **📁 Test Web Root Deployment:**
    *   Locate the web root directory (`htdocs`).
    *   Deploy **2-3 images** of any kind in different directories or subfolders within the web root.
    *   Verify that you can access these images via HTTP (e.g., `http://localhost/image.png` or `http://localhost/subfolder/image.png`).
    *   Take screenshots of your browser displaying these images.

---

## Step II: WordPress & WooCommerce E-shop Installation

1.  **📝 Install WordPress:**
    *   Download and install WordPress locally on your XAMPP server. Follow the [Official WordPress Installation Guide](https://wordpress.org/support/article/how-to-install-wordpress/).
2.  **🛒 Install WooCommerce:**
    *   Add the WooCommerce plugin to your WordPress site. Follow the [WooCommerce Installation Guide](https://woocommerce.com/document/installing-uninstalling-woocommerce/).
3.  **⚙️ Configure WooCommerce:**
    *   Complete the basic onboarding configuration for WooCommerce. Refer to [Getting Started with WooCommerce](https://woocommerce.com/documentation/plugins/woocommerce/getting-started/installation-and-updating/).
4.  **📸 Verify Shop:**
    *   Take a screenshot of your local e-shop running.

---

## Deliverables & Submission
*   Combine all screenshots (image HTTP access screenshots and the final running e-shop screenshot) into a single PDF document.
*   Upload the PDF report to the assignment portal.
