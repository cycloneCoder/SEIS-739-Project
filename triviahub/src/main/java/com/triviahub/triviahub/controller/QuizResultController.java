package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.*;
import com.triviahub.triviahub.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/results")
public class QuizResultController {

    private final QuizResultRepository quizResultRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizResultController(QuizResultRepository quizResultRepository, UserAnswerRepository userAnswerRepository, QuizRepository quizRepository, UserRepository userRepository) {
        this.quizResultRepository = quizResultRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submits a user's answers for a quiz, calculates the score, and saves the result.
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionDTO submission) {
        // --- 1. Validate Input ---
        User user = userRepository.findById(submission.getUserId()).orElse(null);
        Quiz quiz = quizRepository.findById(submission.getQuizId()).orElse(null);
        if (user == null || quiz == null) {
            return ResponseEntity.badRequest().body("Invalid user or quiz ID.");
        }

        // --- 2. Calculate Score ---
        int score = 0;
        List<UserAnswer> userAnswers = new ArrayList<>();
        Map<Long, String> answers = submission.getAnswers();

        for (Question question : quiz.getQuestions()) {
            String selectedAnswer = answers.get(question.getId());
            if (selectedAnswer != null && selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                score++;
            }
        }

        // --- 3. Save the Result ---
        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setQuiz(quiz);
        result.setScore(score);
        result.setCompletedAt(LocalDateTime.now());
        QuizResult savedResult = quizResultRepository.save(result);

        // --- 4. Save the Detailed Answers ---
        for (Question question : quiz.getQuestions()) {
             String selectedAnswer = answers.getOrDefault(question.getId(), "No Answer");
             UserAnswer userAnswer = new UserAnswer(savedResult, question, selectedAnswer);
             userAnswers.add(userAnswer);
        }
        userAnswerRepository.saveAll(userAnswers);
        savedResult.setUserAnswers(userAnswers);


        return ResponseEntity.ok(savedResult);
    }

    /**
     * Retrieves all quiz results for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizResult>> getResultsForUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quizResultRepository.findByUser_Id(userId));
    }
}
