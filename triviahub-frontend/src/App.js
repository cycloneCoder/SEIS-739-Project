import React, { useState } from 'react'; 
import {
  Routes,
  Route,
  useNavigate, 
} from 'react-router-dom';


import LandingPage from './components/LandingPage';
import Register from './components/Register';
import Login from './components/Login';
import QuizList from './components/QuizList';
import Dashboard from './components/Dashboard'; 
import ProtectedRoute from './components/ProtectedRoute'; 
import QuizEditor from './components/QuizEditor';
import QuizPlayer from './components/QuizPlayer';
import MyResults from './components/MyResults';

function App() {
  // 3. Use state for login status. It will check localStorage on first load.
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
  
  // 4. Get the navigate hook to use in handleLogout
  const navigate = useNavigate(); 

  // 5. DEFINE the missing handleLoginSuccess function
  const handleLoginSuccess = () => {
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    // 6. FIX handleLogout to use the state setter function
    setIsLoggedIn(false);
    navigate('/'); //Navigate back to landing page on logout
  };

  return (
    <div>
      <Routes>
        {/* --- Public Routes (No Navbar) --- */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/register" element={<Register />} />
        <Route 
          path="/login" 
          // This prop will now work correctly
          element={<Login onLoginSuccess={handleLoginSuccess} />} 
        />
        
        {/* --- Protected Routes (With Navbar) --- */}
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/quizzes"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <QuizList />
            </ProtectedRoute>
          }
        />
        <Route
          path="/quiz/create"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <QuizEditor />
            </ProtectedRoute>
          }
        />
        <Route
          path="/quiz/edit/:id"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <QuizEditor />
            </ProtectedRoute>
          }
        />
        <Route
          path="/quiz/:id"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <QuizPlayer />
            </ProtectedRoute>
          }
        />
        <Route
          path="/my-results"
          element={
            <ProtectedRoute isLoggedIn={isLoggedIn} onLogout={handleLogout}>
              <MyResults />
            </ProtectedRoute>
          }
        />
      </Routes>
    </div>
  );
}


export default App;