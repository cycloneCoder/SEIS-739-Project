// package com.triviahub.triviahub.controller;

// import com.triviahub.triviahub.model.*;
// import com.triviahub.triviahub.repository.*;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/results")
// public class QuizResultController {

//     private final QuizResultRepository quizResultRepository;
//     private final UserAnswerRepository userAnswerRepository;
//     private final QuizRepository quizRepository;
//     private final UserRepository userRepository;

//     public QuizResultController(QuizResultRepository quizResultRepository, UserAnswerRepository userAnswerRepository, QuizRepository quizRepository, UserRepository userRepository) {
//         this.quizResultRepository = quizResultRepository;
//         this.userAnswerRepository = userAnswerRepository;
//         this.quizRepository = quizRepository;
//         this.userRepository = userRepository;
//     }

//     /**
//      * Submits a user's answers for a quiz, calculates the score, and saves the result.
//      */
//     @PostMapping("/submit")
//     public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionDTO submission) {
//         // --- 1. Validate Input ---
//         User user = userRepository.findById(submission.getUserId()).orElse(null);
//         Quiz quiz = quizRepository.findById(submission.getQuizId()).orElse(null);
//         if (user == null || quiz == null) {
//             return ResponseEntity.badRequest().body("Invalid user or quiz ID.");
//         }

//         // --- 2. Calculate Score ---
//         int score = 0;
//         List<UserAnswer> userAnswers = new ArrayList<>();
//         Map<Long, String> answers = submission.getAnswers();

//         for (Question question : quiz.getQuestions()) {
//             String selectedAnswer = answers.get(question.getId());
//             if (selectedAnswer != null && selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
//                 score++;
//             }
//         }

//         // --- 3. Save the Result ---
//         QuizResult result = new QuizResult();
//         result.setUser(user);
//         result.setQuiz(quiz);
//         result.setScore(score);
//         result.setCompletedAt(LocalDateTime.now());
//         QuizResult savedResult = quizResultRepository.save(result);

//         // --- 4. Save the Detailed Answers ---
//         for (Question question : quiz.getQuestions()) {
//              String selectedAnswer = answers.getOrDefault(question.getId(), "No Answer");
//              UserAnswer userAnswer = new UserAnswer(savedResult, question, selectedAnswer);
//              userAnswers.add(userAnswer);
//         }
//         userAnswerRepository.saveAll(userAnswers);
//         savedResult.setUserAnswers(userAnswers);


//         return ResponseEntity.ok(savedResult);
//     }

//     /**
//      * Retrieves all quiz results for a specific user.
//      */
//     @GetMapping("/user/{userId}")
//     public ResponseEntity<List<QuizResult>> getResultsForUser(@PathVariable Long userId) {
//         if (!userRepository.existsById(userId)) {
//             return ResponseEntity.notFound().build();
//         }
//         return ResponseEntity.ok(quizResultRepository.findByUser_Id(userId));
//     }
// }


package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.*;
import com.triviahub.triviahub.repository.*;
import jakarta.persistence.EntityNotFoundException; // Import EntityNotFoundException
import jakarta.validation.Valid; // Import Valid
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import UsernameNotFoundException
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // Import Principal
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/results") // Consider changing base path later if needed (e.g., /api/results)
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
            // --- 1. Get Authenticated User ---
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                // Should not happen with valid JWT
                 throw new UsernameNotFoundException("User not found: " + username);
            }

            // --- 2. Validate Quiz ---
            Quiz quiz = quizRepository.findById(submission.getQuizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz not found with id: " + submission.getQuizId()));


            // --- 3. Calculate Score ---
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

            // --- 4. Save the Result ---
            QuizResult result = new QuizResult();
            result.setUser(user);
            result.setQuiz(quiz);
            result.setScore(score);
            result.setCompletedAt(LocalDateTime.now());
            // Save the result first to get its ID
            QuizResult savedResult = quizResultRepository.save(result);

            // --- 5. Associate and Save Detailed Answers ---
            for (UserAnswer userAnswer : userAnswers) {
                userAnswer.setQuizResult(savedResult); // Link answer to the saved result
            }
            userAnswerRepository.saveAll(userAnswers);
            // Optionally set the answers back on the result if needed for the response (usually not necessary)
            // savedResult.setUserAnswers(userAnswers);


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
    @GetMapping("/me") // New endpoint for the logged-in user
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
     * Keep this for potential admin use cases, but ensure proper authorization if needed.
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
     * Optional: Add authorization check if only the user who took the quiz can see their detailed result.
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<QuizResult> getResultById(@PathVariable Long resultId /*, Principal principal */) {
        Optional<QuizResult> resultOpt = quizResultRepository.findById(resultId);
        if (resultOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        /* // Optional Authorization Check:
        QuizResult result = resultOpt.get();
        if (!result.getUser().getUsername().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        */

        return ResponseEntity.ok(resultOpt.get());
    }
}
