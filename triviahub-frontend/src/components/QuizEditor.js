// src/components/QuizEditor.js
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import * as api from '../services/apiService';
import Modal from './Modal';
import QuestionForm from './QuestionForm';
import './QuizEditor.css'; // New styles

function QuizEditor() {
  const { id } = useParams(); // Gets the ':id' from the URL
  const navigate = useNavigate();
  
  const [quiz, setQuiz] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [questions, setQuestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editingQuestion, setEditingQuestion] = useState(null); // null for new, question obj for editing

  // Load the quiz data if we are editing
  useEffect(() => {
    if (id) {
      setLoading(true);
      api.fetchQuizById(id)
        .then(response => {
          const { data } = response;
          setQuiz(data);
          setTitle(data.title);
          setDescription(data.description);
          setQuestions(data.questions || []);
        })
        .catch(err => {
          console.error(err);
          setError('Failed to load quiz. You may not be the owner.');
        })
        .finally(() => setLoading(false));
    } else {
      // We are creating a new quiz
      setLoading(false);
    }
  }, [id]);

  // Handler for saving the quiz details (Title/Desc)
  const handleSaveQuizDetails = async () => {
    setError('');
    try {
      if (id) {
        // Update existing quiz
        await api.updateQuiz(id, title, description);
      } else {
        // Create new quiz
        const response = await api.createQuiz(title, description);
        // After creating, redirect to the new edit page
        navigate(`/quiz/edit/${response.data.id}`);
      }
    } catch (err) {
      setError('Failed to save quiz details.');
      console.error(err);
    }
  };

  // Handler for when the QuestionForm is submitted
  const handleQuestionSubmit = async (questionData) => {
    if (editingQuestion) {
      // Update existing question
      const updated = await api.updateQuestion(editingQuestion.id, questionData);
      setQuestions(questions.map(q => q.id === editingQuestion.id ? updated.data : q));
    } else {
      // Create new question
      const newQuestion = await api.createQuestion(questionData);
      setQuestions([...questions, newQuestion.data]);
    }
  };

  const handleOpenModal = (question = null) => {
    setEditingQuestion(question);
    setShowModal(true);
  };
  
  const handleDeleteQuestion = async (questionId) => {
    if (window.confirm('Are you sure you want to delete this question?')) {
      try {
        await api.deleteQuestion(questionId);
        setQuestions(questions.filter(q => q.id !== questionId));
      } catch (err) {
        console.error(err);
        setError('Failed to delete question.');
      }
    }
  };

  if (loading) {
    return <div>Loading Quiz Editor...</div>;
  }
  
  return (
    <div className="quiz-editor-container">
      <h2>{id ? 'Edit Quiz' : 'Create New Quiz'}</h2>
      {error && <p className="editor-error">{error}</p>}

      {/* Quiz Details Form */}
      <div className="quiz-details-form card">
        <h3>Quiz Details</h3>
        <div className="form-group">
          <label>Title</label>
          <input 
            type="text" 
            value={title} 
            onChange={e => setTitle(e.target.value)} 
          />
        </div>
        <div className="form-group">
          <label>Description</label>
          <textarea 
            value={description} 
            onChange={e => setDescription(e.target.value)} 
          />
        </div>
        <button onClick={handleSaveQuizDetails} className="save-btn">
          {id ? 'Save Changes' : 'Create Quiz & Continue'}
        </button>
      </div>

      {/* Questions List (only show if quiz exists) */}
      {id && (
        <div className="questions-list card">
          <div className="questions-header">
            <h3>Questions ({questions.length})</h3>
            <button onClick={() => handleOpenModal(null)} className="add-btn">
              Add Question
            </button>
          </div>
          <ul>
            {questions.map(q => (
              <li key={q.id}>
                <span>{q.questionText}</span>
                <div className="question-buttons">
                  <button onClick={() => handleOpenModal(q)} className="edit-btn">Edit</button>
                  <button onClick={() => handleDeleteQuestion(q.id)} className="delete-btn">Delete</button>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* The Modal for Adding/Editing Questions */}
      {showModal && (
        <Modal onClose={() => setShowModal(false)}>
          <QuestionForm 
            quizId={id}
            question={editingQuestion}
            onSubmit={handleQuestionSubmit}
            onClose={() => setShowModal(false)}
          />
        </Modal>
      )}
    </div>
  );
}

export default QuizEditor;