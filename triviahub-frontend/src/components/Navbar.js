// src/components/Navbar.js
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Navbar.css';

// We pass in the onLogout function from App.js
function Navbar({ onLogout }) {
  const navigate = useNavigate();

  const handleLogoutClick = () => {
    onLogout(); // This will update the app state
    navigate('/'); // Redirect to the landing page
  };

  return (
    <nav className="navbar-container">
      <div className="navbar-brand">
        <Link to="/dashboard">TriviaHub</Link>
      </div>
      <ul className="navbar-links">
        <li>
          <Link to="/dashboard">Dashboard</Link>
        </li>
        <li>
          <Link to="/quizzes">All Quizzes</Link>
        </li>
      </ul>
      <button onClick={handleLogoutClick} className="logout-button">
        Logout
      </button>
    </nav>
  );
}

export default Navbar;