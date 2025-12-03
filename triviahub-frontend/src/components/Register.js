// src/components/Register.js
import React, { useState } from 'react';
import { register } from '../services/apiService';
import { useNavigate } from 'react-router-dom';
import './Register.css';

function Register() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (password != confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    try {
      // Use the register function from your apiService
      // It expects username, email, and password
      await register(username, email, password);

      // On success, automatically navigate to the login page
      navigate('/login');

    } catch (err) {
      if (err.response && err.response.status === 409) {
        setError('Username or email already exists.');
      } else {
        setError('Registration failed. Please try again.');
      }
      console.error('Registration failed:', err);
    }
  };

  return (

    <div className="register-container">
      <form className="register-form" onSubmit={handleSubmit}>
        <h2>Create Account</h2>

        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password (min 6 characters)"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength="6"
          required
        />

        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />

        <button type="submit" className="register-button">Sign Up</button>

        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
  );
}

export default Register;