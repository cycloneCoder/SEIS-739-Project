// src/components/ProtectedRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
import Navbar from './Navbar';

// This component takes the `isLoggedIn` state and the `onLogout` function
// and passes them to the Navbar.
// `children` will be the page component (e.g., <Dashboard />)
function ProtectedRoute({ isLoggedIn, onLogout, children }) {
  if (!isLoggedIn) {
    // If not logged in, redirect to the login page
    return <Navigate to="/login" replace />;
  }

  // If logged in, render the Navbar, and then the page content
  return (
    <div>
      <Navbar onLogout={onLogout} />
      <main>
        {children}
      </main>
    </div>
  );
}

export default ProtectedRoute;