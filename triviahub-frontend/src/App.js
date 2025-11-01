// import logo from './logo.svg';
// import './App.css';

// function App() {
//   return (
//     <div className="App">
//       <header className="App-header">
//         <img src={logo} className="App-logo" alt="logo" />
//         <p>
//           Edit <code>src/App.js</code> and save to reload.
//         </p>
//         <a
//           className="App-link"
//           href="https://reactjs.org"
//           target="_blank"
//           rel="noopener noreferrer"
//         >
//           Learn React
//         </a>
//       </header>
//     </div>
//   );
// }

// export default App;

// src/App.js
// import React from 'react';
// import {
//   BrowserRouter as Router,
//   Routes,
//   Route,
//   Link,
// } from 'react-router-dom';
// import Login from './components/Login';
// import QuizList from './components/QuizList';
// // You would also create and import a Register component
// // import Register from './components/Register';

// function App() {
//   // A simple logout function
//   const handleLogout = () => {
//     localStorage.removeItem('token');
//     // Force a refresh or navigate to login
//     window.location.href = '/login';
//   };

//   return (
//     <Router>
//       <div>
//         <nav>
//           <ul>
//             <li>
//               <Link to="/">Home (Quizzes)</Link>
//             </li>
//             <li>
//               <Link to="/login">Login</Link>
//             </li>
//             {/* You would add a /register link here */}
//             <li>
//               {/* This is a simple way to show logout */}
//               {localStorage.getItem('token') && (
//                 <button onClick={handleLogout}>Logout</button>
//               )}
//             </li>
//           </ul>
//         </nav>

//         <hr />

//         {/* --- Define Your Pages --- */}
//         <Routes>
//           <Route path="/" element={<QuizList />} />
//           <Route path="/login" element={<Login />} />
//           {/* <Route path="/register" element={<Register />} /> */}
//           {/* You would add routes for taking a quiz, e.g., /quiz/:id */}
//         </Routes>
//       </div>
//     </Router>
//   );
// }

// export default App;


// src/App.js
import React from 'react';
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Link,
} from 'react-router-dom';

// Import your new components
import LandingPage from './components/LandingPage';
import Register from './components/Register';
import Login from './components/Login';
import QuizList from './components/QuizList';
import Dashboard from './components/Dashboard'; 
import ProtectedRoute from './components/ProtectedRoute';


function App() {
  const handleLogout = () => {
    localStorage.removeItem('token');
    isLoggedIn(false);
  };

  // Check if user is logged in
  const isLoggedIn = !!localStorage.getItem('token');

return (
    // The nav is GONE from here
    <div>
      <Routes>
        {/* --- Public Routes (No Navbar) --- */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/register" element={<Register />} />
        <Route 
          path="/login" 
          element={<Login onLoginSuccess={handleLoginSuccess} />} 
        />
        
        {/* --- Protected Routes (With Navbar) --- */}
        {/* These routes all go through the ProtectedRoute component.
          If logged in, it shows the Navbar and the requested page.
          If not, it redirects to /login.
        */}
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
      </Routes>
    </div>
  );
}

// Keep the AppWrapper to provide Router context
function AppWrapper() {
  return (
    <Router>
      <App />
    </Router>
  );
}

export default AppWrapper;