// src/components/Dashboard.js
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { fetchMyQuizzes, fetchMyResults } from '../services/apiService';
import './Dashboard.css'; // We'll add some styles

function Dashboard() {
  const [myQuizzes, setMyQuizzes] = useState([]);
  const [myResults, setMyResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

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
        <div className="stat-card">
          <h3>{myResults.length}</h3>
          <p>Quizzes Taken</p>
        </div>
      </div>

      {/* My Quizzes Section */}
      <div className="my-quizzes-section">
        <div className="my-quizzes-header">
          <h3>My Quizzes</h3>
          {/* This is where your create button will go! */}
          <button className="create-quiz-btn">Create New Quiz</button>
        </div>
        <ul className="quiz-list">
          {myQuizzes.length === 0 ? (
            <p>You haven't created any quizzes yet.</p>
          ) : (
            myQuizzes.map((quiz) => (
              <li key={quiz.id} className="quiz-list-item">
                <span>{quiz.title}</span>
                {/* This is where your edit button will go! */}
                <button className="edit-quiz-btn">Edit</button>
              </li>
            ))
          )}
        </ul>
      </div>
    </div>
  );
}

export default Dashboard;