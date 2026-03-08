# 🚀 TrendCrafters

**TrendCrafters** is a Kotlin Android app built with Jetpack Compose that helps content creators discover trending topics, manage their content drafts, and grow their social media presence on Instagram Reels and YouTube Shorts.

---

## 📱 Screenshots & Features

### App Flow
```
Splash Screen → Today's Trends (Landing) → Auth (Login / Sign Up) → Onboarding Questionnaire → Home
```

---

## ✨ Features

### 🎬 Trending Reels Discovery
- Animated video card stack on the landing screen showcasing trending reels
- Videos streamed from AWS S3 using **ExoPlayer (Media3)**
- Auto-rotating card stack with smooth spring animations every 3 seconds
- Tap-to-select video cards with ripple feedback

### 🔐 Authentication
- Full **Login** and **Sign Up** screens with email + password
- Password visibility toggle
- JWT token management via `tokenManager`
- Error handling with inline error messages and loading indicators
- Deep purple glassmorphism UI theme with neon purple gradients

### 🧭 Onboarding Questionnaire
- 7-step personalization questionnaire after sign-up
- Questions covering content niche, target audience, goals, reel style, vibe, experience level, and platform
- Multi-select support (content niches)
- Animated progress bar across questions
- Final step: content strategy description → profile saved via REST API

### 🏠 Home Feed
- Bottom navigation bar with 4 tabs: **Home**, **Drafts**, **Chat**, **Profile**
- Home tab: animated video card stack + live trending hashtag chips
- Hashtags fetched from a `pytrends`-powered backend API
- Interactive hashtag chips with press animations, colors, and rotation effects
- Lottie animation player button with spring bounce effect

### 📝 Drafts Manager
- View, filter, and manage content drafts
- Draft statuses: `Ready ✅`, `In Progress ✏️`, `Needs Review 🔍`
- Filter drafts by status
- Delete drafts with a confirmation dialog
- **Draft Detail Screen** (modal bottom sheet) with:
  - AI-generated content analysis
  - Performance drivers, engagement triggers, and content patterns
  - AI-generated content ideas with "Best Fit" recommendation
  - Optimization tip
  - "Continue in Chat" option to refine drafts via AI chat

### 💬 Chat
- In-app AI chat screen for discussing and refining content ideas

### 👤 Profile
- User profile screen linked to the backend

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | **Kotlin** |
| UI Framework | **Jetpack Compose** + Material 3 |
| Navigation | **Compose Navigation** |
| Networking | **Retrofit 2** + Gson + OkHttp Logging Interceptor |
| Video Playback | **ExoPlayer (Media3)** |
| Animations | **Lottie Compose** |
| Architecture | **MVVM** (ViewModel + StateFlow) |
| Auth | JWT Token Manager |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## 📁 Project Structure

```
com.example.trendcrafters/
├── ApiService/          # Retrofit API interface & client setup
├── Auth/                # Login, SignUp screens, AuthViewModel, token manager
├── Home/                # Home, Drafts, Chat, Profile screens & ViewModels
├── Profile/             # Profile service & data models
├── draft/               # Draft repository & DraftViewModel
├── navigationFiles/     # NavHost & sealed Screens class
├── onboarding/          # TodayTrend landing, ProfieQuestion questionnaire, reelSelection
├── pytrends/            # TrendViewModel (fetches trending hashtags from backend)
├── assets/              # Lottie animation helpers
├── ui/theme/            # Color palette, typography, app theme
└── MainActivity.kt      # App entry point
```

---

## 🚦 Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Android device / emulator running API 24+
- Internet connection (app streams videos from AWS S3 and talks to a backend API)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Jaivesh8/Trend_Setter.git
   cd Trend_Setter
   ```

2. **Open in Android Studio**
   - File → Open → select the `TrendCrafters` folder

3. **Sync Gradle**
   - Android Studio will automatically sync dependencies

4. **Run the app**
   - Select a device/emulator and click ▶️ Run

> **Note:** The app requires network access to load reels from AWS S3 and to communicate with the trend/auth backend. Cleartext traffic is enabled for development purposes.

---

## 🔌 API & Backend

The app communicates with a REST backend for:

| Endpoint | Description |
|---|---|
| Auth | Login & Sign Up |
| Profile | Create / Update user profile after onboarding |
| Trends | Fetch trending hashtag chips (pytrends-powered) |
| Drafts | List, create, and delete content drafts |

Retrofit is configured in `RetrofitClient.kt` with an OkHttp logging interceptor for debug visibility.

---

## 🎨 Design Language

- **Color palette**: Neon Purple (`#9D4EDD`), Deep Purple (`#320B4D`), gradient black backgrounds
- **Glassmorphism**: Semi-transparent cards with subtle white borders
- **Spring animations**: Card stacks, chip presses, and FAB interactions use `Spring.DampingRatioMediumBouncy`
- **Lottie**: MP4 player button powered by a Lottie JSON animation

---

## 📦 Dependencies

```kotlin
// Networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Lottie
implementation("com.airbnb.android:lottie-compose:6.4.0")

// Video Playback (ExoPlayer)
implementation("androidx.media3:media3-exoplayer:1.2.1")
implementation("androidx.media3:media3-ui:1.2.1")
implementation("androidx.media3:media3-common:1.2.1")

// Navigation
implementation("androidx.navigation:navigation-compose:2.8.0")

// Extended Material Icons
implementation("androidx.compose.material:material-icons-extended")
```

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you'd like to change.

---

## 📄 License

This project was built as part of a hackathon. All rights reserved by the TrendCrafters team.
