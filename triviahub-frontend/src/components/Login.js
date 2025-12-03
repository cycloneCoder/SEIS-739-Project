
import React, { useState } from 'react';
import { login } from '../services/apiService';
import { useNavigate } from 'react-router-dom';
import './Login.css';

function Login({ onLoginSuccess }) {
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
      //localStorage.setItem('token', token);

      //Update to use session storage instead
      sessionStorage.setItem('token',token);


      onLoginSuccess();

      // Redirect to the home page (or quiz list)
      navigate('/dashboard');

    } catch (err) {
      setError('Invalid username or password');
      console.error('Login failed:', err);
    }
  };

  return (
  //   <form onSubmit={handleSubmit}>
  //     <h2>Login</h2>
  //     <input
  //       type="text"
  //       placeholder="Username"
  //       value={username}
  //       onChange={(e) => setUsername(e.target.value)}
  //       required
  //     />
  //     <input
  //       type="password"
  //       placeholder="Password"
  //       value={password}
  //       onChange={(e) => setPassword(e.target.value)}
  //       required
  //     />
  //     <button type="submit">Login</button>
  //     {error && <p style={{ color: 'red' }}>{error}</p>}
  //   </form>
  // );
  <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
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
        
        <button type="submit" className="login-button">Login</button>
        
        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
  );
}

export default Login;