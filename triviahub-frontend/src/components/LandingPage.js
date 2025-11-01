// src/components/LandingPage.js
import React from 'react';
import { Link } from 'react-router-dom';
import './LandingPage.css'; 

function LandingPage() {
  return (
    <div className="landing-container">
      <header className="landing-header">
        <h1>Welcome to TriviaHub!</h1>
        <p>Create, share, and play quizzes on any topic.</p>
      </header>
      <main className="landing-actions">
        <Link to="/login" className="landing-button login">
          Login
        </Link>
        <Link to="/register" className="landing-button register">
          Create Account
        </Link>
      </main>
    </div>
  );
}

export default LandingPage;