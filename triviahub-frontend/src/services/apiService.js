// src/services/apiService.js
import axios from 'axios';

// Create an 'instance' of axios
const api = axios.create({
  baseURL: 'http://localhost:8080', // Your Spring Boot backend URL
});

// Add a request interceptor to include the token in headers
api.interceptors.request.use(
  (config) => {
    // Get the token from local storage
    //const token = localStorage.getItem('token');

    //Updating to use session storage
    const token = sessionStorage.getItem('token');



    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// --- Auth Service Endpoints ---

export const login = (username, password) => {
  return api.post('/api/auth/login', { username, password });
};

export const register = (username, email, password) => {
  return api.post('/api/auth/register', { username, email, password });
};

// --- Quiz Service Endpoints ---

export const fetchAllQuizSummaries = () => {
  return api.get('/quizzes');
};

export const fetchQuizById = (quizId) => {
  return api.get(`/quizzes/${quizId}`);
};

// --- Dashboard Service Endpoints ---

// This hits the GET /quizzes/my-quizzes endpoint
export const fetchMyQuizzes = () => {
  return api.get('/quizzes/my-quizzes');
};

// This hits the GET /results/me endpoint
export const fetchMyResults = () => {
  return api.get('/results/me');
};

// --- Add other endpoints here as you build... ---

// --- Quiz CRUD Endpoints ---

// Creates a new blank quiz
export const createQuiz = (title, description) => {
  return api.post('/quizzes', { title, description });
};

// Updates a quiz's title or description
export const updateQuiz = (quizId, title, description) => {
  return api.put(`/quizzes/${quizId}`, { title, description });
};

// Deletes an entire quiz
export const deleteQuiz = (quizId) => {
  return api.delete(`/quizzes/${quizId}`);
};

// --- Question CRUD Endpoints ---

// Creates a new question for a specific quiz
export const createQuestion = (questionData) => {
  // questionData should match QuestionDTO:
  // { questionText, optionA, optionB, optionC, optionD, correctAnswer, quizId }
  return api.post('/questions', questionData);
};

// Updates an existing question
export const updateQuestion = (questionId, questionData) => {
  // questionData should match QuestionDTO
  return api.put(`/questions/${questionId}`, questionData);
};

// Deletes a question
export const deleteQuestion = (questionId) => {
  return api.delete(`/questions/${questionId}`);
};

// --- Quiz Taking Endpoint ---

export const submitQuiz = (quizId, answersMap) => {
  // The DTO on the backend expects { quizId, answers }
  const submission = {
    quizId: quizId,
    answers: answersMap 
  };
  return api.post('/results/submit', submission);
};


export default api;