// src/components/Login.js
import React, { useState } from 'react';
import { login } from '../services/apiService';
// You'll need useNavigate to redirect after login
import { useNavigate } from 'react-router-dom';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      // Use your apiService
      const response = await login(username, password);

      // Your backend returns a response like { token: "..." }
      const { token } = response.data;

      // Store the token in local storage
      localStorage.setItem('token', token);

      // Redirect to the home page (or quiz list)
      navigate('/');

    } catch (err) {
      setError('Invalid username or password');
      console.error('Login failed:', err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Login</h2>
      <input
        type="text"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit">Login</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
}

export default Login;