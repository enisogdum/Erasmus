# GroupGo — Multi-modal Transit Planner & Group Expense Splitter

GroupGo is a premium mobile application designed to help users plan journeys, compare transportation options dynamically, manage travel groups, track trip histories, and split expenses evenly among group members. By integrating a personalized **Time-Value Calculator**, the application allows users to evaluate routing options not just by price, but by the real monetary value of their travel time.

---

## 🌟 Key Features

### 🗺️ Multi-modal Route Planner & Maps
*   **OSMDroid Map Integration:** Visualizes origin and destination locations using interactive open-source maps.
*   **Multi-Modal Routing:** Queries OpenStreetMap Routing Machine (OSRM) for Car, Bicycle, and Foot routes.
*   **Transit Simulation:** Dynamically models Public Transit, Taxi/Ride-share, and Flights (for distances ≥ 400 km).
*   **Interactive Location Search:** Integrated place autocomplete (geocoding suggestions) to easily locate landmarks and cities.
*   **Active GPS Tracking:** Support for permissions-based live location tracking.

### ⏱️ Time-Value Calculator
*   **Personalized Rate Calculation:** Computes a net hourly earnings rate using monthly salary and working hours stored in Jetpack DataStore.
*   **Time-Value Scoring:** For every travel option, computes a `timeValueScore` (`durationHours * hourlyRate`). Allows sorting and filtering routes by **Fastest**, **Cheapest**, or **Balanced** scores.

### 👥 Travel Groups & Expense Sharing
*   **Group Ledger:** Create travel groups with custom member names to track shared journeys.
*   **Even-Split Algorithm:** Every recorded trip cost is split equally among group members. The app maintains a ledger of total paid amounts and who owes whom.
*   **Debt Settlement Engine:** Features a greedy debt-minimization settlement algorithm (`calculateSettlement`) that automatically calculates the fewest number of peer-to-peer transfers required to settle all debts.
*   **Manual Paid Adjustments:** Allows members to edit their total paid amounts to settle up outside the routing ledger.

### 🔐 Secure Auth & User Tiers
*   **Hybrid Authentication:** Implements Firebase Authentication for security, with fallback local storage using deterministic hashing in `SharedPreferences`.
*   **Registration Tiers:** Users can register as **Free** or **Premium**. Free users verify their email via Firebase email verification.
*   **Brevo SMTP Integration:** Uses Brevo's SMTP API to send custom password reset verification codes.

---

## 🏗️ Architecture & Project Structure

The project strictly follows **Clean Architecture** principles, decoupled into three layers:

```
app/src/main/java/com/example/groupgo/
│
├── di/                   # Dependency Injection modules (Hilt)
│   ├── AppModule.kt      # Provides Room Database, Retrofit API client, OkHttpClient
│   └── RepositoryModule.kt # Binds interfaces to repository implementations
│
├── domain/               # Domain Layer (Pure Kotlin, Framework independent)
│   ├── model/            # Enterprise data structures (UserSettings, TravelGroup, TripRecord, etc.)
│   └── repository/       # Repository interfaces defining business contracts
│
├── data/                 # Data Layer (Database, Network, SharedPrefs, DataStore)
│   ├── local/            # Room Database setup, Daos (Group, Trip, Expense), Entities, and Mappers
│   ├── remote/           # Retrofit service API, DTOs, and Route Mappers
│   └── repository/       # Concrete implementation of domain repositories
│
└── ui/                   # Presentation Layer (Jetpack Compose, MVVM)
    ├── navigation/       # AppNavGraph using Jetpack Navigation Compose
    ├── theme/            # Theme, Color Palette, Typography configuration
    ├── login/            # Screen, ViewModel, and State for User Authentication
    ├── home/             # Main Map, Route Comparison, & Booking Screen
    ├── groups/           # Groups list, Member creation, & Settlement calculations
    ├── balance/          # Ledger screen to view and edit member paid amounts
    └── settings/         # Datastore profile settings (Salary, Working Hours)
```

---

## 🛠️ Technology Stack

*   **Language:** Kotlin (JVM Toolchain 21)
*   **UI Framework:** Jetpack Compose (Material 3, Vector Icons Extended)
*   **Dependency Injection:** Dagger Hilt
*   **Database:** Room (SQLite abstraction for persistent offline storage)
*   **Network:** Retrofit 2 & OkHttp 3 (with Logging Interceptor)
*   **Local Storage:** Jetpack DataStore Preferences (settings) & SharedPreferences (auth)
*   **Map & Location:** OSMDroid & Google Play Services Location (Fused Location Client)
*   **Authentication:** Firebase Auth SDK
*   **Email Client:** Brevo SMTP API

---

## 🚀 Build & Installation

### Prerequisites
*   Android Studio (Ladybug or newer recommended)
*   JDK 21
*   Android Device or Emulator running API 26 (Android 8.0) or higher

### Step-by-Step Setup

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/your-username/GroupGo.git
    cd GroupGo
    ```

2.  **Configure API Keys:**
    Create a `local.properties` file in the root directory of the project (if it doesn't already exist) and define the following variables:
    ```properties
    # OpenRouteService API key (if needed, otherwise uses OSM demo routing)
    ORS_API_KEY=your_open_route_service_key

    # Brevo API configuration for custom verification & reset emails
    BREVO_API_KEY=your_brevo_smtp_api_key
    BREVO_SENDER_EMAIL=your_brevo_sender_email@domain.com
    ```
    *Note: These properties are automatically read during build-time by `app/build.gradle.kts` and exposed as variables in `BuildConfig`.*

3.  **Build and Sync Gradle:**
    *   Open the project in Android Studio.
    *   Let the Gradle Sync finish successfully.
    *   Select `app` from the run configurations dropdown.

4.  **Run the Project:**
    Click the **Run** button (green play icon) or use `Shift + F10` to deploy onto your connected device or emulator.

---

## 📊 Course Information

This application is developed as a course project for **Mobile Application Development** (Class 3: Data Model & API Contracts) at the **West Pomeranian University of Technology, Szczecin**.
