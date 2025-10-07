<div align="center">

  <h1> 
    <img width="500" height="500" alt="Trivora_Logo" src="https://github.com/user-attachments/assets/090c1a83-6bc8-48bc-9a23-57ea6e66587f" />
  </h1>

  <h2> Trivora - Interactive Trivia Game Application</h2>
</div>

---

## 🧭 Overview

**Trivora** is a modern trivia game application designed to challenge users’ knowledge across multiple categories while providing an interactive and enjoyable experience.  
The app aims to make learning fun through quizzes, leaderboards, and gamification — allowing users to test their knowledge and compete with others globally.

This README serves as a **comprehensive project report**, detailing the purpose, design considerations, and the use of **GitHub** and **GitHub Actions** in ensuring smooth collaboration, testing, and continuous integration.

---

## 🎬 YouTube Demonstration

▶️ [Watch the Demo Video](https://youtu.be/kKGOq5szSEg)

---

## 👥 Team Members

- Lindokuhle Moyana  
- Christinah Chitombi  
- Keira Wilmot  
- Nqobani Moyane  

---

## Purpose of the Application

The purpose of Trivora is to create a **knowledge-driven entertainment platform** that helps users learn while having fun.  
It combines **education, competition, and personalization** by allowing users to:

---

##  Application Features

### 1. User Authentication and Registration
- Account creation using email and password.  
- Email verification (planned).  
- Persistent login sessions.  
- Google Single Sign-On.  
- Biometric authentication (planned).  
- Password reset via email (planned).

### 2. User Profile
- Update username, profile picture, and email.  

### 3. Settings
- Sound preferences (on/off).  
- Notification toggle (planned).  
- Language switch (English + South African language — planned).  
- Dark/light theme (planned).  
- Select difficulty level.

### 4. Trivia Gameplay
- Multiple categories (History, Sports, Movies, Science).  
- Time-based questions (planned).  
- Image-based questions (planned).  
- Randomized questions and one-question “speed quiz”.  
- Hint system with coins (planned).

### 5. Leaderboards and Gamification (Planned)
- Display top players and scores globally.  
- Achievement badges for high scores and streaks.  
- Social sharing and monthly challenges.

### 6. Offline Mode (Planned)
- Local data storage for offline quizzes.  
- Sync progress when online.

### 7. API and Database
- REST API for authentication, quiz fetching, and score submission.  
- Tables: Users, Questions, Categories, Scores (planned).  
- HTTPS-enabled communication for secure data transfer.

---

## 🧰 GitHub & GitHub Actions Usage

### 🗂️ Version Control with GitHub
GitHub was utilized for:
- **Source code management** and team collaboration.  
- **Branching strategy** for development, testing, and production.  
- **Pull Requests** for code reviews before merging to `main`.

Each feature was developed in its own branch, ensuring clean integration and reducing merge conflicts.

### 🤖 Continuous Integration with GitHub Actions
GitHub Actions was configured to:
- Automatically **build the project** and **run tests** when changes are pushed.  
- Detect build issues early across environments.  
- Generate APK builds and upload them as **artifacts** for easy testing.


