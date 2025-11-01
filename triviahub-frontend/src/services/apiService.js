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
    const token = localStorage.getItem('token');
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

export default api;