<div align="center">

  <h1> <img width="500" height="500" alt="Trivora_Logo" src="https://github.com/user-attachments/assets/090c1a83-6bc8-48bc-9a23-57ea6e66587f" />
 </h1>

</div>

## Overview

The Trivora application is a trivia game designed to provide users with an engaging and challenging experience. This README file outlines the applications features, API design, and architecture of the application. 

### 👥 Team Members
- Lindokuhle Moyana  
- Christinah Chitombi  
- Keira Wilmot   
- Nqobani Moyane  
   
## Application Features

### 1. User Authentication and Registration

-   Users are be able to create an account using email and password.
-   Email verification should be in place to confirm account ownership.
-   Login sessions must remain active until the user logs out manually.
-   Users are able to log in quickly using Single Sign-On (SSO) options like Google.
-   Biometric authentication (fingerprint or face recognition) should be available for faster, more secure login.
-   Password reset must be available via email in case users forget their credentials.

### 2. User Profile
-   Users are able to update personal information (username, profile picture, email).

### 3. Settings

 - Change sound preferences (on/off)
- Enable or disable notifications
-  Switch app language (English + 1 South African language)
-  Select dark or light themed options.

### 3. Trivia Gameplay Features

-   The Trivia game offers multiple trivia categories (e.g., History, Sports, Movies, Science).
-   Each quiz question includes:
    -   A question prompt (text or image-based)
    -   Four multiple-choice answers
    -   A timer for time-based challenges
-   Image-based questions must be included, inspired by 100 PICS.
-   Levels should become more difficult as users progress.
-   A point and reward system must be included:
    -   Correct answers earn coins or hints
    -   Daily login rewards to encourage regular play
-   Users should have access to hints for difficult questions using earned coins.
-   Daily challenges should be available for extra rewards.

### 4. Leaderboards and Gamification

-   A global leaderboard must display the top players and their scores.
-   Achievement badges should be awarded for:
    -   High scores
    -   Daily streaks
    -   Completing certain categories
-   Users should be able to share their achievements on social media.
-   Weekly or monthly challenges can encourage ongoing engagement.

### 5. Offline Mode with Sync

-   Users must be able to play the game offline with saved progress.
-   Game data should sync with the server once the user is back online.
-   Local storage using RoomDB or SQLite must store questions, user scores, and settings for offline play.

### 6. Push Notifications

-   Daily reminders for challenges or rewards must be sent to users.
-   Notifications should be sent through Firebase Cloud Messaging.
-   Users should be able to turn notifications on or off in the settings.

### 7. Multi-Language Support

-   The app must support at least two languages: English and one South African language.
-   Users should be able to switch languages from the settings menu.

### 8. Trivia Question Management

-   Questions should be stored in a database with the following fields:
    -   Question ID
    -   Category
    -   Question Text
    -   Four multiple-choice answers
    -   Correct Answer
    -   Image URL (if applicable)
-   Admins should be able to add new questions without requiring an app update.
-   Categories should be expandable so new topics can be added easily.

### 9. API and Database Requirements

-   The backend must be built with a REST API for:
    -   User registration and login
    -   Fetching trivia questions and categories
    -   Submitting user scores
    -   Leaderboard data retrieval
-   Database tables must include:
    -   Users
    -   Questions
    -   Categories
    -   User Scores
-   The API must be hosted on a server with HTTPS for secure connections.

### 10. Data Storage and Security

-   Sensitive data like passwords must be encrypted before storage.
-   User progress and settings should be stored both locally (for offline play) and on the server (for backups).
-   Authentication tokens should be used for secure API communication.

### 11. User Interface and Experience

-   The interface should be simple and easy to navigate.
-   Material Design principles should be used for a modern look.
-   The game must work on different screen sizes, from small phones to tablets.
-   Animation and sound effects should make the experience more engaging.

### 12. Reporting and Analytics

-   Track user statistics like most played categories
-   Store analytics in the backend for reporting and improvement purposes.

 app can call.

