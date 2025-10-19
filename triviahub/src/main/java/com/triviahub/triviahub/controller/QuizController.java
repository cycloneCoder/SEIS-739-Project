package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.Quiz;
import com.triviahub.triviahub.model.QuizDTO;
import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.QuizRepository;
import com.triviahub.triviahub.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quizzes")
public class QuizController {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    public QuizController(QuizRepository quizRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new quiz.
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
        Optional<User> userOpt = userRepository.findById(quizDTO.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found with id: " + quizDTO.getUserId());
        }

        Quiz quiz = new Quiz(quizDTO.getTitle(), quizDTO.getDescription(), userOpt.get());
        Quiz savedQuiz = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuiz);
    }

    /**
     * Gets a list of all available quizzes.
     */
    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    /**
     * Gets a single quiz by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a quiz by its ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        if (!quizRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        quizRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
