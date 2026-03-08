# 🚀 Trend Setter

**Discover Trends. Turn Ideas into Content.**

Trend Setter is a **Kotlin-based Android mobile application** that helps
content creators discover trending topics, hashtags, and content ideas
while organizing their creative workflow in one place.

The platform combines a **modern Android frontend** with a **FastAPI
backend** to deliver real-time trend insights and content management
tools.

------------------------------------------------------------------------

# 📌 Overview

Trend Setter bridges the gap between **trend discovery and content
creation** by enabling creators to:

-   Discover trending hashtags and keywords
-   Explore trends across niches
-   Save and organize content ideas
-   Manage drafts for future posts
-   Convert trend insights into structured content

------------------------------------------------------------------------

# 🏗 System Architecture

Android App (Kotlin + Jetpack Compose) │ │ REST API ▼ FastAPI Backend │
├── Authentication (JWT) ├── Profile Management ├── Draft Management └──
Trending Data (PyTrends) │ ▼ PostgreSQL Database

------------------------------------------------------------------------

# 📱 Frontend (Android Application)

The Android application provides a modern UI for creators to discover
trends and organize content ideas.

Built using **Kotlin + Jetpack Compose** with smooth animations and a
creator-focused workflow.

------------------------------------------------------------------------

# ✨ Frontend Features

## 🎬 Trending Reels Discovery

-   Animated video card stack
-   Trending reels streamed from AWS S3
-   Auto-rotating cards every 3 seconds
-   Smooth spring animations

## 🔐 Authentication

-   Login and Signup
-   JWT-based authentication
-   Password visibility toggle
-   Error handling and loading indicators

## 🧭 Onboarding Personalization

7-step questionnaire that collects:

-   Content niche
-   Target audience
-   Platform preference
-   Creator goals
-   Experience level
-   Content style

## 🏠 Home Dashboard

Creator workspace that includes:

-   Trending reels
-   Live trending hashtags
-   Interactive hashtag chips
-   AI-powered content inspiration

## 📝 Draft Manager

Creators can:

-   Create drafts
-   Update drafts
-   Delete drafts
-   View saved drafts

Draft statuses:

-   Ready
-   In Progress
-   Needs Review

## 💬 AI Chat Assistant

Helps creators:

-   Generate reel ideas
-   Improve captions
-   Brainstorm viral content

------------------------------------------------------------------------

# 🛠 Frontend Tech Stack

  Layer          Technology
  -------------- -----------------------
  Language       Kotlin
  UI             Jetpack Compose
  Architecture   MVVM
  Networking     Retrofit + Gson
  Video          ExoPlayer
  Animations     Lottie
  Navigation     Compose Navigation
  State          ViewModel + StateFlow

------------------------------------------------------------------------

# ⚙️ Backend (FastAPI)

The backend powers authentication, user profiles, drafts, and trend
discovery.

It exposes REST APIs that the Android application consumes.

------------------------------------------------------------------------

# ✨ Backend Features

## 🔐 Authentication

-   User signup
-   Login
-   JWT-based authentication
-   OAuth2 password flow

## 👤 User Profiles

Users can:

-   Store profile information
-   Select preferred content niches
-   Customize creator preferences

## 📝 Draft Management

Backend APIs support:

-   Creating drafts
-   Updating drafts
-   Fetching drafts
-   Deleting drafts

## 📈 Trending Insights

Trending data is collected using **Google Trends (PyTrends)**.

The backend provides:

-   Trending hashtags
-   Trending keywords
-   Emerging topics

------------------------------------------------------------------------

# 🏗 Backend Tech Stack

  Category         Technology
  ---------------- --------------
  Language         Python
  Framework        FastAPI
  ORM              SQLAlchemy
  Validation       Pydantic
  Authentication   JWT + OAuth2
  Database         PostgreSQL
  Trend Data       PyTrends
  Deployment       AWS EC2
  Server           Uvicorn

------------------------------------------------------------------------

# ▶️ Running the Backend

Start the server:

uvicorn app.main:app --reload

Server runs at:

http://127.0.0.1:8000

API Docs:

http://127.0.0.1:8000/docs

------------------------------------------------------------------------

# 🔌 API Endpoints

Authentication POST /auth/signup POST /auth/login

Profiles GET /profile PUT /profile

Drafts POST /drafts GET /drafts GET /drafts/{{id}} PUT /drafts/{{id}}
DELETE /drafts/{{id}}

Trends GET /trending/hashtags GET /trending/keywords

------------------------------------------------------------------------

# 👨‍💻 Authors
Ayush Poddar\
Computer Science Engineering Student

https://github.com/ayush2006-creator

Jaivesh Chopra\
Computer Engineering Student

GitHub: https://github.com/Jaivesh8

------------------------------------------------------------------------

# 📄 License

This project was built as part of a **hackathon project**.
