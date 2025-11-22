import React, { useState, useEffect } from 'react';
//import { Link } from 'react-router-dom';
import { Link, useNavigate } from 'react-router-dom';
//import { fetchMyQuizzes, fetchMyResults } from '../services/apiService';
import './Dashboard.css'; 
import { fetchMyQuizzes, fetchMyResults, deleteQuiz } from '../services/apiService';

function Dashboard() {
  const [myQuizzes, setMyQuizzes] = useState([]);
  const [myResults, setMyResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        setLoading(true);
        // Fire off both requests at the same time
        const [quizzesResponse, resultsResponse] = await Promise.all([
          fetchMyQuizzes(),
          fetchMyResults()
        ]);
        
        setMyQuizzes(quizzesResponse.data);
        setMyResults(resultsResponse.data);
      } catch (err) {
        console.error('Failed to load dashboard data:', err);
        setError('Could not load dashboard. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  const handleDeleteQuiz = async (quizId) => {
    // Add a confirmation dialog to prevent accidental deletion
    if (window.confirm('Are you sure you want to delete this quiz permanently?')) {
      try {
        await deleteQuiz(quizId);
        // On success, update the state to remove the quiz from the list
        // This makes the UI update instantly without a page reload
        setMyQuizzes(myQuizzes.filter(quiz => quiz.id !== quizId));
      } catch (err) {
        console.error('Failed to delete quiz:', err);
        // You could set an error message here if you want
        // setError('Failed to delete quiz. Please try again.');
      }
    }
  };


  if (loading) {
    return <div>Loading Dashboard...</div>;
  }

  if (error) {
    return <div style={{ color: 'red' }}>{error}</div>;
  }

  return (
    <div className="dashboard-container">
      <h2>My Dashboard</h2>
      
      {/* Stats Section */}
      <div className="dashboard-stats">
        <div className="stat-card">
          <h3>{myQuizzes.length}</h3>
          <p>Quizzes Created</p>
        </div>
        <Link to="/my-results" className="stat-card-link">
          <div className="stat-card">
            <h3>{myResults.length}</h3>
            <p>Quizzes Taken</p>
          </div>
        </Link>
        {/* <div className="stat-card">
          <h3>{myResults.length}</h3>
          <p>Quizzes Taken</p>
        </div> */}
      </div>

      {/* My Quizzes Section */}
      <div className="my-quizzes-section">
        <div className="my-quizzes-header">
          <h3>My Quizzes</h3>
          {/* This is where your create button will go! */}
          <button 
            className="create-quiz-btn"
            onClick={() => navigate('/quiz/create')}
          >Create New Quiz</button>
        </div>
        <ul className="quiz-list">
          {myQuizzes.length === 0 ? (
            <p>You haven't created any quizzes yet.</p>
          ) : (
            myQuizzes.map((quiz) => (
              <li key={quiz.id} className="quiz-list-item">
                <span>{quiz.title}</span>
                <div className="quiz-actions"> 
                  <button 
                    className="take-quiz-btn"
                    onClick={() => navigate(`/quiz/${quiz.id}`)}
                  >
                    Take Quiz
                  </button>
                  <button 
                    className="edit-quiz-btn"
                    onClick={() => navigate(`/quiz/edit/${quiz.id}`)}
                  >
                    Edit
                  </button>
                  <button 
                    className="delete-quiz-btn"
                    onClick={() => handleDeleteQuiz(quiz.id)}
                  >
                    Delete
                  </button>
                </div>
              </li>
            ))
          )}
        </ul>
      </div>
    </div>
  );
}

export default Dashboard;