# TriviaHub

## 🔎 Overview

TriviaHub is a full-stack trivia application that allows users to create, manage, and play quizzes. It features a secure authentication system, a dashboard for managing content, and a detailed results tracking system.

## 🏗️ Projct Architecutre 
- **Backend:** Java 21, Springboot 3.5.6, PostgreSQL, Gradle
- **Frontend:** React.js, React Router, Axios
- **Authentication:** Spring Security with JWT (JSON Web Tokens)
- **API Documentation:** OpenAPI/Swagger UI

## 📂 Project Structure
```
SEIS-739-Project/
├── triviahub/                 # Backend Code
│   ├── src/main/java/         # Java Source (Controllers, Models, Services)
│   ├── src/main/resources/    # Config (application.properties)
│   └── build.gradle           # Dependencies
│
└── triviahub-frontend/        # Frontend Code
    ├── src/components/        # React Components (Login, Dashboard, QuizPlayer)
    ├── src/services/          # API Service (Axios configuration)
    └── package.json           # Frontend Dependencies
```    

## 📋 Prerequisites
Before running the application, ensure you have the following installed:
- Java Development Kit (JDK) 21
- Node.js (v14 or higher) and npm
- PostgreSQL Database Server (https://www.postgresql.org/download/)

## 🏃‍♂️‍➡️ Getting Started

### 1. Database configuration

TriviaHub is configured to connect to a local PostgreSQL database
1. Open your PostgreSQL tool (pgAdmin, psql, etc.)
2. Create a database named `triviahub`
3. Create a user (or update `src/main/resources/application.properties` to match your existing credentials)

#### Default Database configuration
- **Database Name:** `triviahub`
- **Username:** `triviahub_user`
- **Password:** `securepassword`
- **Port:** `5432`

### 2. Running the backend (Spring Boot)
1. After opening TriviaHub in an IDE of your choice, open a terminal and navigate to the backend directory:
```bash
cd triviahub
```
2. Build and run the application using Gradle
- Mac/Linux
    ```bash
    ./gradlew bootRun
    ```
- Windows
    ```bash
    gradlew.bat bootRun
    ```
3. The backend server will start at `http://localhost:8080`

**API Documentation:** Once the backend is running you can view the Swagger API Documentation at: `http://localhost:8080/swagger-ui.html`

### 3. Running the frontend (React)
1. Open a new terminal window (keep the backend running) and navigate to the frontend directory:
```bash
cd triviahub-frontend
```
2. Install dependencies
```bash 
npm install
```
3. Start the server
```bash
npm start
```
4. The application should automatically open in your browser at `http://localhost:3000`

## 🚀 Usage Guide 
1. **Registration:** On the landing page, click "Create Account" to register a new user.
2. **Login:** Log in with your new credentials.
3. **Dashboard:**
    - **Create Quiz:** Click "Create New Quiz" and add a title and description
    - **Add Questions:** Add multiple-choice questions (A,B,C,D) and select which option is the correct answer
    - **Save your Quiz:** Click the "Save Changes" button to save your quiz
    - **Take a Quiz**: Return to your dashboard or click the "All Quizzes" navigation bar option to find a list of quizzes to take!
    - **View Results:** Check your "My Quiz History" to see your scores anc when you last completed a quiz. 
    - **Editing or Deleting a quiz:** Your quizzes can be edited or deleted using the buttons on the user dashboard. 




