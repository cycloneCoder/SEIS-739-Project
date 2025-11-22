import React, { useState, useEffect } from 'react';
import { fetchMyResults } from '../services/apiService';
import './MyResults.css'; // New styles

function MyResults() {
  const [latestResults, setLatestResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadResults = async () => {
      try {
        setLoading(true);
        const response = await fetchMyResults();
        
        // This logic finds the latest attempt for each quiz
        const resultsMap = new Map();
        response.data.forEach(result => {
          const quizId = result.quiz.id;
          const existing = resultsMap.get(quizId);
          
          // If this result is newer than the one in the map, replace it
          if (!existing || new Date(result.completedAt) > new Date(existing.completedAt)) {
            resultsMap.set(quizId, result);
          }
        });

        // Convert the Map values back into an array for rendering
        setLatestResults(Array.from(resultsMap.values()));

      } catch (err) {
        console.error('Failed to load results:', err);
        setError('Could not load your quiz results.');
      } finally {
        setLoading(false);
      }
    };

    loadResults();
  }, []);

  if (loading) {
    return <div>Loading results...</div>;
  }

  if (error) {
    return <div className="results-container"><p className="results-error">{error}</p></div>;
  }
  
  // Helper to format the date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="results-container">
      <h2>My Quiz History</h2>
      
      <div className="results-list card">
        {latestResults.length === 0 ? (
          <p>You haven't taken any quizzes yet.</p>
        ) : (
          <ul className="results-ul">
            <li className="results-header">
              <span>Quiz Title</span>
              <span>Your Score</span>
              <span>Last Attempt</span>
            </li>
            {latestResults.map(result => (
              <li key={result.id} className="results-item">
                <span>{result.quiz.title}</span>
                <span>{result.score} / {result.quiz.questions.length}</span>
                <span>{formatDate(result.completedAt)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default MyResults;