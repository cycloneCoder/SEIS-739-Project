// src/components/QuizList.js
import React, { useState, useEffect } from 'react';
import { fetchAllQuizSummaries } from '../services/apiService';
import { Link } from 'react-router-dom'; 
import './QuizList.css'; 

function QuizList() {
  const [quizzes, setQuizzes] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const getQuizzes = async () => {
      try {
        // This request will automatically include the token (if it exists)
        // thanks to your apiService interceptor!
        const response = await fetchAllQuizSummaries();
        setQuizzes(response.data); // Your backend sends a list of QuizSummaryDTO
      } catch (error) {
        console.error('Failed to fetch quizzes:', error);
        // You might get a 403 Forbidden if the token is bad or missing
      } finally {
        setLoading(false);
      }
    };

    getQuizzes();
  }, []); // The empty array [] means this runs once on component mount

  if (loading) {
    return <p>Loading quizzes...</p>;
  }

  return (
    // <div>
    //   <h2>Available Quizzes</h2>
    //   <ul>
    //     {quizzes.map((quiz) => (
    //       <li key={quiz.id}>
    //         <h3>{quiz.title}</h3>
    //         <p>{quiz.description}</p>
    //         <p>
    //           <i>Created by: {quiz.createdByUsername}</i>
    //         </p>
    //       </li>
    //     ))}
    //   </ul>
    // </div>
    <div className="quiz-list-container">
      <h2>All Quizzes</h2>
      <ul className="quiz-list-ul">
        {quizzes.map((quiz) => (
          // Make the whole list item a clickable link
          <Link to={`/quiz/${quiz.id}`} key={quiz.id} className="quiz-card-link">
            <li className="quiz-card">
              <h3>{quiz.title}</h3>
              <p>{quiz.description}</p>
              <span>By: {quiz.createdByUsername}</span>
            </li>
          </Link>
        ))}
      </ul>
    </div>
  );
}

export default QuizList;