# What is Yummizi (ymz)?
Yummizi was a foodtech startup project that allowed restaurants to prepare diners' food while they were on their way to the establishment. Although I didn't continue with the project due to validation issues, it taught me a lot about marketing, sales, and full-stack development.

I think that the best way to be grateful for what I have is to share it. Here, I share with you what I learned by building a stable, real-time platform that connects diners with restaurants the Yummizi way.

# Sub-systems of the platform
+ [AWS Lambda with hono](https://github.com/PieroEngineer/ymz-aws-lambda-with-hono) (TS) 
+ [AWS serverless configuration with AWS SAM](https://github.com/PieroEngineer/ymz-iac-with-aws-sam) (yaml) 
+ [Client for process observability](https://github.com/PieroEngineer/ymz-admin-client-with-python) (Python) 
+ [Landing page](https://github.com/PieroEngineer/ymz-landing-page) (HTML + JS + CSS) 
+ [Restaurant Web app](https://github.com/PieroEngineer/ymz-restaurant-client-side) (TS + Vite + React) 
+ [Diner Android app](https://github.com/PieroEngineer/ymz-diner-client-side) (Kotlin)

# Yummizi sample videos (Spanish)

+ [Diner's side](https://youtu.be/7b_l_gql7mM?si=8SZDDcmm5-jZD5qW) 🍽️
+ [Restaurant's side](https://youtu.be/fAfyB8eCOTE?si=P1Oo85OMT-clABDw) 🏪
  
# 🍽️ YummiziApp

**YummiziApp** is a modern, high-performance Android mobile application built for seamless restaurant discovery, interactive food ordering, digital payment check-ins, and real-time order tracking.

Built from the ground up using **Modern Android Development (MAD)** practices, the app features Jetpack Compose, MVVM with Unidirectional Data Flow (UDF), AWS Amplify Cognito authentication, Room local caching, Retrofit networking with custom JWT token interception, and Ably WebSocket realtime updates.

---

## 🌟 Features

- **🔐 Authentication & User Management**: Secure Sign Up / Sign In via **AWS Amplify (Cognito)** with token-based session management and custom `TokenInterceptor`.
- **🍽️ Restaurant Discovery & Dynamic Feed**: Categorized restaurant listings, interactive image reels, and restaurant preview screens.
- **🥗 Interactive Food Selection**: Rich menu views with customizable complements, dish specifics, extra commentary inputs, and cost calculation.
- **💳 Payment Preparation & Check-in**: Streamlined checkout workflow with receipt submission and payment state synchronization.
- **⏱️ Real-Time Arrival & Queue Tracking**: Live request status tracking powered by **Ably WebSockets**, accompanied by interactive arrival timers and progress indicators.
- **⚙️ User Profile & Specifications**: User preferences, dietary filters (`AvailableSpecificsToken`), and contact/feedback modals.

---

## 🏗️ Architecture & Tech Stack

The project strictly adheres to **MVVM Architecture** combined with **Unidirectional Data Flow (UDF)** and a clean modular directory layout (`core/` + `features/`).

### 📱 Core Technologies

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.4.0 (Java 17 target) |
| **UI Framework** | Jetpack Compose (BOM `2026.02.01`) + Material 3 |
| **Navigation** | Navigation Compose with Type-Safe `@Serializable` Routes |
| **Dependency Injection** | Dagger-Hilt (`2.59.2`) |
| **Realtime Communication** | Ably WebSockets SDK (`1.2.40`) |
| **Cloud Authentication** | AWS Amplify Auth Cognito (`2.21.0`) |
| **Local Database** | Room Database (`2.8.4`) with KSP schema exports |
| **Local Key-Value** | Jetpack DataStore Preferences (`1.2.1`) |
| **HTTP Client & API** | Retrofit 2 + OkHttp 4 + Kotlinx Serialization |
| **Image Loading** | Coil 3 (`3.0.4`) with OkHttp network fetcher |
| **Analytics** | Firebase Analytics (BoM) |

---

## 📂 Project Structure

```
yummiziApp/
├── app/
│   ├── schemas/                            # Room database schema exports
│   └── src/main/java/com/yummizi/app/
│       ├── MainActivity.kt                 # Entry activity
│       ├── YummiziApp.kt                   # Application class & Amplify initialization
│       ├── YummiziNavHost.kt               # Type-safe Navigation Graph
│       ├── core/                           # Shared core modules
│       │   ├── data/                       # Repositories, DAOs, DB, API Network & DI
│       │   │   ├── di/                     # Hilt Modules (Network, Database, Realtime, DataStore)
│       │   │   ├── local/                  # Room DB, DAOs, Entities, Converters
│       │   │   ├── network/                # Retrofit APIs, TokenInterceptor, Ably DataSource
│       │   │   └── repository/             # Concrete Repository implementations
│       │   ├── designsystem/               # Color, Typography, Spacing, Custom Theme
│       │   ├── ui/                         # Shared UI Components (Buttons, Modals, Snackbars)
│       │   └── utils/                      # Form Validators, Currency & Extension Converters
│       └── features/                       # Feature-based domain & UI components
│           ├── appconfig/                  # App initialization config
│           ├── auth/                       # Auth Selection, Email Login/SignUp
│           ├── contact/                    # Contact & Share modal
│           ├── home/                       # Feed screen, active restaurants, scrollables
│           ├── intro/                      # Onboarding carousel
│           ├── main/                       # Splash & Main ViewModels
│           ├── navigation/                 # Type-safe YummiziRoute definitions
│           ├── payment/                    # Payment Preparation & Arrival tracking
│           ├── restaurant/                 # Interactive Menu & Food selection
│           └── user/                       # User profile & dietary specification
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Ladybug / Panda or newer
- **JDK**: Version 17
- **Android SDK**: Min SDK 24, Target SDK 36 (Compile SDK 36)

### Environment Configuration (`local.properties`)

Create or update your `local.properties` file in the root directory with the following keys:

```properties
# Backend API Gateway
API_GATEWAY_URL=https://your-api-gateway-url.com/prod/

# Ably WebSockets API Key (Development)
ABLY_API_KEY=your_ably_api_key_here

# Release Signing Keystore (Optional for local debug builds)
KEYSTORE_PATH=path/to/keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=your_key_alias
KEY_PASSWORD=your_key_password
```

---

## 📄 License

This project is proprietary and confidential. All rights reserved.

---

# Author

Made with 💙 by **Piero Olivas Tuesta**
