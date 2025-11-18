
import React, { useState, useEffect } from 'react';
import './QuestionForm.css';

// `quizId` is for creating new questions
// `question` is the data for an existing question to edit
// `onSubmit` is the function to call the API
// `onClose` closes the modal
function QuestionForm({ quizId, question, onSubmit, onClose }) {
  const [questionText, setQuestionText] = useState('');
  const [optionA, setOptionA] = useState('');
  const [optionB, setOptionB] = useState('');
  const [optionC, setOptionC] = useState('');
  const [optionD, setOptionD] = useState('');
  const [correctAnswer, setCorrectAnswer] = useState('A');
  const [error, setError] = useState('');

  const isEditing = !!question; // True if we're editing, false if creating

  useEffect(() => {
    // If a question prop is passed, fill the form for editing
    if (isEditing) {
      setQuestionText(question.questionText);
      setOptionA(question.optionA);
      setOptionB(question.optionB);
      setOptionC(question.optionC || '');
      setOptionD(question.optionD || '');
      setCorrectAnswer(question.correctAnswer);
    }
  }, [question, isEditing]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!optionA || !optionB) {
      setError('Option A and Option B are required.');
      return;
    }

    const questionData = {
      questionText,
      optionA,
      optionB,
      optionC: optionC || null, // Send null if empty
      optionD: optionD || null, // Send null if empty
      correctAnswer,
      quizId: isEditing ? question.quizId : quizId // Use the correct quizId
    };

    try {
      // Call the onSubmit prop, which will be our API function
      await onSubmit(questionData);
      onClose(); // Close the modal on success
    } catch (err) {
      setError('Failed to save question. Please try again.');
      console.error(err);
    }
  };

  return (
    <form className="question-form" onSubmit={handleSubmit}>
      <h3>{isEditing ? 'Edit Question' : 'Add New Question'}</h3>
      <div className="form-group">
        <label>Question</label>
        <textarea
          value={questionText}
          onChange={(e) => setQuestionText(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label>Option A (Required)</label>
        <input
          type="text"
          value={optionA}
          onChange={(e) => setOptionA(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label>Option B (Required)</label>
        <input
          type="text"
          value={optionB}
          onChange={(e) => setOptionB(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label>Option C (Optional)</label>
        <input
          type="text"
          value={optionC}
          onChange={(e) => setOptionC(e.target.value)}
        />
      </div>
      <div className="form-group">
        <label>Option D (Optional)</label>
        <input
          type="text"
          value={optionD}
          onChange={(e) => setOptionD(e.target.value)}
        />
      </div>
      <div className="form-group">
        <label>Correct Answer</label>
        <select
          value={correctAnswer}
          onChange={(e) => setCorrectAnswer(e.target.value)}
        >
          <option value="A">A</option>
          <option value="B">B</option>
          {optionC && <option value="C">C</option>}
          {optionD && <option value="D">D</option>}
        </select>
      </div>
      {error && <p className="form-error">{error}</p>}
      <button type="submit" className="form-submit-btn">
        {isEditing ? 'Save Changes' : 'Create Question'}
      </button>
    </form>
  );
}

export default QuestionForm;