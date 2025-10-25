// package com.triviahub.triviahub.controller;

// import com.triviahub.triviahub.model.Quiz;
// import com.triviahub.triviahub.model.QuizDTO;
// import com.triviahub.triviahub.model.User;
// import com.triviahub.triviahub.repository.QuizRepository;
// import com.triviahub.triviahub.repository.UserRepository;

// import jakarta.validation.Valid;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/quizzes")
// public class QuizController {

//     private final QuizRepository quizRepository;
//     private final UserRepository userRepository;

//     public QuizController(QuizRepository quizRepository, UserRepository userRepository) {
//         this.quizRepository = quizRepository;
//         this.userRepository = userRepository;
//     }

//     /**
//      * Creates a new quiz.
//      */
//     @PostMapping
//     public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDTO quizDTO) {
//         Optional<User> userOpt = userRepository.findById(quizDTO.getUserId());
//         if (userOpt.isEmpty()) {
//             return ResponseEntity.badRequest().body("User not found with id: " + quizDTO.getUserId());
//         }

//         Quiz quiz = new Quiz(quizDTO.getTitle(), quizDTO.getDescription(), userOpt.get());
//         Quiz savedQuiz = quizRepository.save(quiz);
//         return ResponseEntity.status(HttpStatus.CREATED).body(savedQuiz);
//     }

//     /**
//      * Gets a list of all available quizzes.
//      */
//     @GetMapping
//     public List<Quiz> getAllQuizzes() {
//         return quizRepository.findAll();
//     }

//     /**
//      * Gets a single quiz by its ID.
//      */
//     @GetMapping("/{id}")
//     public ResponseEntity<Quiz> getQuizById(@PathVariable Long id) {
//         return quizRepository.findById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     /**
//      * Deletes a quiz by its ID.
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
//         if (!quizRepository.existsById(id)) {
//             return ResponseEntity.notFound().build();
//         }
//         quizRepository.deleteById(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.Quiz;
import com.triviahub.triviahub.model.QuizDTO;
import com.triviahub.triviahub.model.QuizSummaryDTO;
import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.QuizRepository;
import com.triviahub.triviahub.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
     * The user is identified from the JWT token, not from the request body.
     */
    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizDTO quizDTO, Principal principal) {
        // Get the username from the security context
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        Quiz quiz = new Quiz(quizDTO.getTitle(), quizDTO.getDescription(), user);
        Quiz savedQuiz = quizRepository.save(quiz);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuiz);
    }

    /**
     * Gets a list of all available quizzes.
     */
    // @GetMapping
    // public List<Quiz> getAllQuizzes() {
    //     return quizRepository.findAll();
    // }


    /*
     * New GET Mapping for returning quiz summaries instead of full quiz JSON
     */
    @GetMapping
    public List<QuizSummaryDTO> getAllQuizzes() {
        return quizRepository.findAllQuizSummaries();
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
     * TODO: Add authorization logic to ensure only the creator can delete the quiz.
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
