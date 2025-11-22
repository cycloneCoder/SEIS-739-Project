import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { fetchQuizById, submitQuiz } from '../services/apiService';
import './QuizPlayer.css'; // New styles

function QuizPlayer() {
  const { id } = useParams(); // Get quiz ID from URL
  const navigate = useNavigate();
  
  const [quiz, setQuiz] = useState(null);
  const [questions, setQuestions] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(0);
  const [selectedAnswers, setSelectedAnswers] = useState({}); // { questionId: "A" }
  const [isFinished, setIsFinished] = useState(false);
  const [finalScore, setFinalScore] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // 1. Fetch the quiz data on load
  useEffect(() => {
    setLoading(true);
    fetchQuizById(id)
      .then(response => {
        setQuiz(response.data);
        setQuestions(response.data.questions || []);
        if (response.data.questions.length === 0) {
          setError("This quiz has no questions!");
        }
      })
      .catch(err => {
        console.error(err);
        setError("Failed to load quiz.");
      })
      .finally(() => setLoading(false));
  }, [id]);

  // 2. Handle answer selection
  const handleSelectAnswer = (questionId, optionKey) => {
    setSelectedAnswers({
      ...selectedAnswers,
      [questionId]: optionKey
    });
  };

  // 3. Handle moving to the next question
  const handleNextQuestion = () => {
    if (currentQuestionIndex < questions.length - 1) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  // 4. Handle submitting the quiz
  const handleSubmitQuiz = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await submitQuiz(id, selectedAnswers);
      setFinalScore(response.data.score); // Backend returns the QuizResult with the score
      setIsFinished(true);
    } catch (err) {
      console.error("Failed to submit quiz:", err);
      setError("Failed to submit quiz. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const getOptionText = (question, key) => {
    switch (key) {
      case 'A': return question.optionA;
      case 'B': return question.optionB;
      case 'C': return question.optionC;
      case 'D': return question.optionD;
      default: return '';
    }
  };

  if (loading && !isFinished) {
    return <div>Loading Quiz...</div>;
  }

  if (error) {
    return <div className="quiz-player-container"><p className="quiz-error">{error}</p></div>;
  }

  // --- Show Final Score Screen ---
  // if (isFinished) {
  //   return (
  //     <div className="quiz-player-container">
  //       <h2>Quiz Complete!</h2>
  //       <div className="final-score">
  //         <p>Your Score:</p>
  //         <h3>{finalScore} / {questions.length}</h3>
  //       </div>
  //       <button className="quiz-button" onClick={() => navigate('/dashboard')}>
  //         Back to Dashboard
  //       </button>
  //     </div>
  //   );
  // }

  // --- Show Final Score & Review Screen ---
  if (isFinished) {
    return (
      <div className="quiz-player-container"> {/* Removed 'card' class here to let children handle it */}
        
        {/* Score Card */}
        <div className="card score-card">
          <h2>Quiz Complete!</h2>
          <div className="final-score">
            <p>Your Score:</p>
            <h3>{finalScore} / {questions.length}</h3>
          </div>
          <button className="quiz-button" onClick={() => navigate('/dashboard')}>
            Back to Dashboard
          </button>
        </div>

        {/* Review Section */}
        <div className="review-section">
          <h3>Review Answers</h3>
          <ul className="review-list">
            {questions.map((q, index) => {
              const userAnswer = selectedAnswers[q.id];
              const isCorrect = userAnswer === q.correctAnswer;

              return (
                <li key={q.id} className={`card review-item ${isCorrect ? 'review-success' : 'review-failure'}`}>
                  <h4>{index + 1}. {q.questionText}</h4>
                  
                  <div className="review-details">
                    {/* User's Answer */}
                    <p>
                      <strong>Your Answer: </strong>
                      <span className={isCorrect ? 'text-green' : 'text-red'}>
                        {userAnswer}: {getOptionText(q, userAnswer)}
                      </span>
                    </p>
                    
                    {/* Correct Answer (only show if user was wrong) */}
                    {!isCorrect && (
                      <p>
                        <strong>Correct Answer: </strong>
                        <span className="text-green">
                          {q.correctAnswer}: {getOptionText(q, q.correctAnswer)}
                        </span>
                      </p>
                    )}
                  </div>
                </li>
              );
            })}
          </ul>
        </div>

      </div>
    );
  }


  // --- Show Quiz Question Screen ---
  const currentQuestion = questions[currentQuestionIndex];
  if (!currentQuestion) {
    return <div className="quiz-player-container card"><h2>Loading question...</h2></div>
  }

  // Helper to render options
  const renderOptions = () => {
    const options = [
      { key: 'A', text: currentQuestion.optionA },
      { key: 'B', text: currentQuestion.optionB },
    ];
    if (currentQuestion.optionC) options.push({ key: 'C', text: currentQuestion.optionC });
    if (currentQuestion.optionD) options.push({ key: 'D', text: currentQuestion.optionD });

    return options.map(opt => (
      <li 
        key={opt.key}
        className={`option-item ${selectedAnswers[currentQuestion.id] === opt.key ? 'selected' : ''}`}
        onClick={() => handleSelectAnswer(currentQuestion.id, opt.key)}
      >
        <span className="option-key">{opt.key}</span>
        <span className="option-text">{opt.text}</span>
      </li>
    ));
  };

  const isLastQuestion = currentQuestionIndex === questions.length - 1;

  return (
    <div className="quiz-player-container">
      <div className="card">
        <div className="quiz-header">
          <h3>{quiz.title}</h3>
          <p>Question {currentQuestionIndex + 1} of {questions.length}</p>
        </div>
        
        <div className="question-area">
          <h4 className="question-text">{currentQuestion.questionText}</h4>
          <ul className="options-list">
            {renderOptions()}
          </ul>
        </div>
        
        <div className="quiz-navigation">
          {isLastQuestion ? (
            <button 
              className="quiz-button submit"
              onClick={handleSubmitQuiz}
              disabled={!selectedAnswers[currentQuestion.id]} // Disable if no answer selected
            >
              Submit Quiz
            </button>
          ) : (
            <button 
              className="quiz-button"
              onClick={handleNextQuestion}
              disabled={!selectedAnswers[currentQuestion.id]} // Disable if no answer selected
            >
              Next
            </button>
          )}
        </div>
      </div>
    </div>
  );
}

export default QuizPlayer;