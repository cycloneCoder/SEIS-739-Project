package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.*;
import com.triviahub.triviahub.repository.*;
import jakarta.persistence.EntityNotFoundException; 
import jakarta.validation.Valid; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.web.bind.annotation.*;

import java.security.Principal; 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Submits the authenticated user's answers for a quiz, calculates the score, and saves the result.
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@Valid @RequestBody QuizSubmissionDTO submission, Principal principal) {
        try {
            //Get Authenticated User
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                 throw new UsernameNotFoundException("User not found: " + username);
            }

            //Validate Quiz
            Quiz quiz = quizRepository.findById(submission.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + submission.getQuizId()));


            //Calculate Score
            int score = 0;
            List<UserAnswer> userAnswers = new ArrayList<>();
            Map<Long, String> submittedAnswers = submission.getAnswers();
            List<Question> actualQuestions = quiz.getQuestions(); // Get questions directly from the fetched quiz

             if (actualQuestions == null || actualQuestions.isEmpty()) {
                 return ResponseEntity.badRequest().body("Cannot submit results for a quiz with no questions.");
            }

            for (Question question : actualQuestions) {
                String selectedAnswer = submittedAnswers.get(question.getId());
                boolean isCorrect = selectedAnswer != null && selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer());
                if (isCorrect) {
                    score++;
                }
                // Always create a UserAnswer record
                String answerToStore = selectedAnswer != null ? selectedAnswer : "No Answer";
                 userAnswers.add(new UserAnswer(null, question, answerToStore)); // QuizResult is set later
            }

            //Save the Result
            QuizResult result = new QuizResult();
            result.setUser(user);
            result.setQuiz(quiz);
            result.setScore(score);
            result.setCompletedAt(LocalDateTime.now());
            QuizResult savedResult = quizResultRepository.save(result);

            //Associate and Save Detailed Answers
            for (UserAnswer userAnswer : userAnswers) {
                userAnswer.setQuizResult(savedResult); 
            }
            userAnswerRepository.saveAll(userAnswers);


            return ResponseEntity.ok(savedResult);

        } catch (EntityNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log exception details
             System.err.println("Error submitting quiz results: " + e.getMessage());
             e.printStackTrace();
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while submitting the quiz.");
        }
    }

    /**
     * Retrieves all quiz results for the currently authenticated user.
     */
    @GetMapping("/me") 
    public ResponseEntity<List<QuizResult>> getMyResults(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or throw exception
        }
        return ResponseEntity.ok(quizResultRepository.findByUser_Id(user.getId()));
    }


    /**
     * Retrieves all quiz results for a specific user by ID.
     * This is only for app admin use
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizResult>> getResultsForUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quizResultRepository.findByUser_Id(userId));
    }

     /**
     * Retrieves a single, detailed QuizResult by its ID.
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResult> getResultById(@PathVariable Long resultId /*, Principal principal */) {
        Optional<QuizResult> resultOpt = quizResultRepository.findById(resultId);
        if (resultOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resultOpt.get());
    }
}
