package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.Quiz;
import com.triviahub.triviahub.model.QuizDTO;
import com.triviahub.triviahub.model.QuizSummaryDTO;
import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.QuizRepository;
import com.triviahub.triviahub.repository.UserRepository;

//import jakarta.persistence.EntityNotFoundException; // Import EntityNotFoundException
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.AccessDeniedException; // Import AccessDeniedException
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
     * Creates a new quiz for the currently authenticated user.
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDTO quizDTO, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            // Should generally not happen if JWT filter is working
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Quiz quiz = new Quiz(quizDTO.getTitle(), quizDTO.getDescription(), user);
        Quiz savedQuiz = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuiz);
    }

    /**
     * Gets a simplified list of all available quizzes for display.
     */
    @GetMapping
    public List<QuizSummaryDTO> getAllQuizzes() {
        return quizRepository.findAllQuizSummaries();
    }

    /**
     * Gets a single, full-detail quiz by its ID (including all questions).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
        return quizRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Gets all quizzes created by the currently authenticated user.
     */
    @GetMapping("/my-quizzes")
    public ResponseEntity<?> getMyQuizzes(Principal principal) {
        // Find the user from the security principal
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Use the existing repository method to find their quizzes
        List<Quiz> quizzes = quizRepository.findByCreatedBy_Id(user.getId());
        
        // Return the full quiz objects 
        return ResponseEntity.ok(quizzes);
    }

    /**
     * Deletes a quiz by its ID, but only if the requesting user is the creator.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id, Principal principal) {
        Optional<Quiz> quizOpt = quizRepository.findById(id);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();

        String currentUsername = principal.getName();

        User creator = quiz.getCreatedBy();
        if (creator == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not determine quiz creator.");
        }
        String creatorUsername = creator.getUsername();


        if (!currentUsername.equals(creatorUsername)) {
             // If usernames don't match, deny access
             // Return 403 Forbidden
             return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this quiz.");
        }

        //If authorized, delete the quiz
        quizRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long id, @Valid @RequestBody QuizDTO quizDTO, Principal principal) {
        Optional<Quiz> quizOpt = quizRepository.findById(id);
        if (quizOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Quiz quiz = quizOpt.get();

        String currentUsername = principal.getName();
        User creator = quiz.getCreatedBy(); 
         if (creator == null) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not determine quiz creator.");
        }
        String creatorUsername = creator.getUsername();


        if (!currentUsername.equals(creatorUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to update this quiz.");
        }

        quiz.setTitle(quizDTO.getTitle());
        quiz.setDescription(quizDTO.getDescription());
        Quiz updatedQuiz = quizRepository.save(quiz);
        return ResponseEntity.ok(updatedQuiz);
    }
  
}
