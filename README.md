<div align="center">

  <h1> <img width="500" height="500" alt="Trivora_Logo" src="https://github.com/user-attachments/assets/090c1a83-6bc8-48bc-9a23-57ea6e66587f" />
 </h1>

</div>

## Overview

The Trivora application is a trivia game designed to provide users with an engaging and challenging experience. This README file outlines the applications features, API design, and architecture of the application. 

##Youtube Video Link
https://youtu.be/kKGOq5szSEg

### 👥 Team Members
- Lindokuhle Moyana  
- Christinah Chitombi  
- Keira Wilmot   
- Nqobani Moyane  
   
## Application Features

### 1. User Authentication and Registration

-   Users are be able to create an account using email and password.
-   Email verification will be in place to confirm account ownership. (Will be Implemented)
-   Login sessions remains active until the user logs out manually.
-   Users are able to log in quickly using Single Sign-On (SSO) options like Google.
-   Biometric authentication (fingerprint or face recognition) should be available for faster, more secure login.(Will be Implemented)
-   Password reset will be available via email in case users forget their credentials.(Will be Implemented)

### 2. User Profile
-   Users are able to update personal information (username, profile picture, email).

### 3. Settings

 - Change sound preferences (on/off)
- Enable or disable notifications (Will be Implemented)
-  Switch app language (English + 1 South African language)(Will be Implemented)
-  Select dark or light themed options.(Will be Implemented)
-  Users are able to choose the difficulties of the questions

### 3. Trivia Gameplay Features

-   The Trivia game offers multiple trivia categories (e.g., History, Sports, Movies, Science).
-   Each quiz question includes:
    -   A question prompt 
    -   Four multiple-choice answers
    -   A timer for time-based challenges (Will be Implemented)
-   Image-based questions will be included. (Will be Implemented)
-   Users should have access to hints for difficult questions using earned coins. (Will be Implemented)
-   Users are able to do a quick one question speed quiz.
-   Questions can be randomized without selecting a specific category

### 4. Leaderboards and Gamification (Will be Implemented)

-   A global leaderboard must display the top players and their scores.
-   Achievement badges should be awarded for:
    -   High scores
    -   Daily streaks
    -   Completing certain categories
-   Users should be able to share their achievements on social media.
-   Weekly or monthly challenges can encourage ongoing engagement.

### 5. Offline Mode with Sync (Will be Implemented)

-   Users must be able to play the game offline with saved progress.
-   Game data should sync with the server once the user is back online.
-   Local storage using RoomDB or SQLite must store questions, user scores, and settings for offline play.


### 6. Multi-Language Support

-   The app must support at least two languages: English and one South African language.
-   Users should be able to switch languages from the settings menu.

### 7. API and Database Requirements

-   The backend is built with a REST API for:
    -   User registration and login
    -   Fetching trivia questions and categories
    -   Submitting user scores
    -   Leaderboard data retrieval (Will be Implemented)
-   Database tables include:
    -   Users
    -   Questions
    -   Categories
    -   User Scores  (Will be Implemented)
-   The API must be hosted on a server with HTTPS for secure connections.

### 8. User Interface and Experience

-   The interface is simple and easy to navigate.
-   Material Design principles should be used for a modern look.


 app can call.

