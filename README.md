# Compounding Growth

Compounding Growth is a native Android personal finance tracking application built to empower users to seamlessly monitor their aggregate net worth, monthly cash flow, and localized budget limits in real time. 

By unifying secure cloud databases with live equity market data pipelines, the application delivers a highly interactive dashboard featuring sophisticated data visualization and secure cross-user transaction sharing.

---

## 🚀 Key Mobile Engineering Focuses

This project was developed with a strong emphasis on modern Android engineering paradigms, robust architectural design, and production-ready performance considerations:

* **Architecture & Pattern Enforcement:** Built entirely using the **MVVM (Model-View-ViewModel)** architectural pattern. Business logic and database states are lifecycle-decoupled via `ViewModel` implementations, utilizing `MediatorLiveData` and `MutableLiveData` to handle reactive UI updates efficiently.
* **State & Data Synchronization:** Implemented real-time reactive asynchronous observers using Android Lifecycle libraries. The app securely abstracts user identity schemas away from backend dependencies, ensuring that if underlying authentication providers change, client-facing views remain stable.
* **Robust Input Validation & User Experience:** Integrated localized data validation mechanisms—such as strict XML view bounds styling, system-managed native `DatePickerDialog` elements, and dedicated numeric input restriction configurations—to eliminate multi-tier input sanitization overhead.

---

## 🛠️ Tech Stack & Architecture

### Core Engineering
* **Language:** Native Kotlin 
* **Minimum SDK Supported:** API 34 (Android 14)
* **UI Framework:** XML Layouts with View Binding configuration
* **Navigation:** Android Jetpack Navigation Components implementing type-safe argument passing (`SafeArgs`) across an optimized single-activity navigation graph.

### Third-Party Architectures & Libraries
* **Database & Auth Core (Firebase Environment):**
    * **Firebase Auth & FirebaseUI:** Handles seamless, lifecycle-aware multi-step authentication processes.
    * **Cloud Firestore:** Manages schema collections for transactions and budgeting, leveraging composite query filtering logic based on access permissions.
* **Network Infrastructure (Retrofit2 & OkHttp3):** Architected standard `HttpUrl` network interfaces paired with specialized `HttpLoggingInterceptor` layers to implement robust HTTP query parsing.
* **Advanced Data Visualization (MPAndroidChart):** Powered real-time line charts tracking multi-asset net worth and investment performance over time, alongside customized horizontal bar charts managing immediate budget overhead tracking.

---

## 📊 Deep-Dive Technical Implementation Highlights

### 1. Asynchronous API Mapping (Alpha Vantage)
To provide real-time investment calculations without crashing under stringent rate limits, the repository abstracts asset pricing via an automated asynchronous mapping layer. 
* **The Challenge:** Explicit JSON parsing configurations mismatching field variables natively.
* **The Resolution:** Applied targeted `@SerializedName` metadata interceptors allowing clean Retrofit object deserialization. Implemented a dynamic `FocusChange` listener configuration on text inputs rather than naive `TextWatcher` hooks, optimizing outbound endpoint calls and entirely avoiding redundant billing or daily API limit utilization.

### 2. Multi-User Shared Access Architecture
Transactions natively feature shared permissions tracking. Firestore instances utilize query filtration that filters matching elements where a user is either marked as the definitive `ownerUid` or registered as an authorized third-party `viewer`. This structure allows a secondary client to view shared metrics affecting joint expenses, while maintaining the flexibility to seamlessly drop active viewership without modifying the underlying primary ledger record.

### 3. Native Canvas Layout Optimization
To resolve dynamic canvas clipping bugs standard to floating-point calculations within complex data grids, the rendering loop utilizes programmatic view paddings (`setExtraOffsets`). This accurately scales and isolates line and bar graphs within strict XML constraints, avoiding metric overflow errors even during dramatic income-to-expense imbalances.

---

## 📈 System Composition (cloc report)
```text
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Kotlin                          17            411             58           1606
XML                             13            102              0            593
-------------------------------------------------------------------------------
SUM:                            30            513             58           2199
-------------------------------------------------------------------------------