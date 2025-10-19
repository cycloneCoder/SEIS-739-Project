// package com.triviahub.triviahub.controller;

// import com.triviahub.triviahub.model.Question;
// import com.triviahub.triviahub.model.User;
// import com.triviahub.triviahub.repository.QuestionRepository;
// import com.triviahub.triviahub.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/questions")
// public class QuestionController {

//     @Autowired
//     private QuestionRepository questionRepository;

//     @Autowired
//     private UserRepository userRepository;

//     // Create question (associate with a user)
//     @PostMapping
//     public ResponseEntity<?> createQuestion(@RequestBody Question question, @RequestParam("userId") Long userId) {
//         Optional<User> userOpt = userRepository.findById(userId);
//         if (userOpt.isEmpty()) {
//             return ResponseEntity.badRequest().body("User not found");
//         }

//         question.setCreatedBy(userOpt.get());
//         Question savedQuestion = questionRepository.save(question);
//         return ResponseEntity.ok(savedQuestion);
//     }

//     // Get all questions
//     @GetMapping
//     public List<Question> getAllQuestions() {
//         return questionRepository.findAll();
//     }

//     // Get specific question
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
//         return questionRepository.findById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     // Update question
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id, @RequestBody Question updatedQuestion) {
//         return questionRepository.findById(id).map(existing -> {
//             existing.setQuestionText(updatedQuestion.getQuestionText());
//             existing.setOptionA(updatedQuestion.getOptionA());
//             existing.setOptionB(updatedQuestion.getOptionB());
//             existing.setOptionC(updatedQuestion.getOptionC());
//             existing.setOptionD(updatedQuestion.getOptionD());
//             existing.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
//             questionRepository.save(existing);
//             return ResponseEntity.ok(existing);
//         }).orElse(ResponseEntity.notFound().build());
//     }

//     // Delete question
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
//         if (!questionRepository.existsById(id)) {
//             return ResponseEntity.notFound().build();
//         }
//         questionRepository.deleteById(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// package com.triviahub.triviahub.controller;

// import com.triviahub.triviahub.model.Question;
// import com.triviahub.triviahub.model.QuestionDTO;
// import com.triviahub.triviahub.model.User;
// import com.triviahub.triviahub.repository.QuestionRepository;
// import com.triviahub.triviahub.repository.UserRepository;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.util.List;

// @RestController
// @RequestMapping("/questions")
// public class QuestionController {

//     private final QuestionRepository questionRepository;
//     private final UserRepository userRepository;

//     public QuestionController(QuestionRepository questionRepository, UserRepository userRepository) {
//         this.questionRepository = questionRepository;
//         this.userRepository = userRepository;
//     }

//     // Create a new question
//     @PostMapping
//     public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto) {
//         try {
//             User user = userRepository.findById(dto.getUserId())
//                     .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));

//             Question question = new Question(
//                 dto.getQuestionText(),
//                 dto.getOptionA(),
//                 dto.getOptionB(),
//                 dto.getOptionC(),
//                 dto.getOptionD(),
//                 dto.getCorrectAnswer(),
//                 user
//             );

//             Question savedQuestion = questionRepository.save(question);
//             return ResponseEntity.ok(savedQuestion);

//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body("Error creating question: " + e.getMessage());
//         }
//     }

//     // Get all questions
//     @GetMapping
//     public List<Question> getAllQuestions() {
//         return questionRepository.findAll();
//     }

//     // Get question by ID
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
//         return questionRepository.findById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     // Update question
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id, @RequestBody QuestionDTO dto) {
//         try {
//             Question existing = questionRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

//             existing.setQuestionText(dto.getQuestionText());
//             existing.setOptionA(dto.getOptionA());
//             existing.setOptionB(dto.getOptionB());
//             existing.setOptionC(dto.getOptionC());
//             existing.setOptionD(dto.getOptionD());
//             existing.setCorrectAnswer(dto.getCorrectAnswer());

//             Question updated = questionRepository.save(existing);
//             return ResponseEntity.ok(updated);

//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body("Error updating question: " + e.getMessage());
//         }
//     }

//     // Delete question
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
//         try {
//             if (!questionRepository.existsById(id)) {
//                 return ResponseEntity.notFound().build();
//             }
//             questionRepository.deleteById(id);
//             return ResponseEntity.ok("Question deleted successfully");
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body("Error deleting question: " + e.getMessage());
//         }
//     }
// }

package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.Question;
import com.triviahub.triviahub.model.QuestionDTO;
import com.triviahub.triviahub.model.Quiz;
import com.triviahub.triviahub.repository.QuestionRepository;
import com.triviahub.triviahub.repository.QuizRepository; // Import the new QuizRepository

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository; // Add the QuizRepository

    // Updated constructor to inject QuizRepository
    public QuestionController(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    /**
     * Creates a new question and associates it with an existing quiz.
     * @param dto The QuestionDTO containing the question details and the ID of the quiz it belongs to.
     * @return The saved Question entity.
     */
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto) {
        try {
            // Step 1: Find the Quiz that this question will belong to.
            Quiz quiz = quizRepository.findById(dto.getQuizId())
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + dto.getQuizId()));

            // Step 2: Create the new Question entity, passing the fetched Quiz object.
            // This now matches the updated Question constructor.
            Question question = new Question(
                dto.getQuestionText(),
                dto.getOptionA(),
                dto.getOptionB(),
                dto.getOptionC(),
                dto.getOptionD(),
                dto.getCorrectAnswer(),
                quiz // Pass the correct Quiz object
            );

            Question savedQuestion = questionRepository.save(question);
            return ResponseEntity.ok(savedQuestion);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating question: " + e.getMessage());
        }
    }

    // Get all questions (This endpoint might be less useful now, consider /quizzes/{id}/questions instead)
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Get question by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        return questionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing question.
     * Note: This implementation does not change the quiz the question is associated with.
     * @param id The ID of the question to update.
     * @param dto The DTO with the updated text fields.
     * @return The updated Question entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id, @RequestBody QuestionDTO dto) {
        try {
            Question existing = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

            existing.setQuestionText(dto.getQuestionText());
            existing.setOptionA(dto.getOptionA());
            existing.setOptionB(dto.getOptionB());
            existing.setOptionC(dto.getOptionC());
            existing.setOptionD(dto.getOptionD());
            existing.setCorrectAnswer(dto.getCorrectAnswer());

            Question updated = questionRepository.save(existing);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating question: " + e.getMessage());
        }
    }

    // Delete question
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        try {
            if (!questionRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            questionRepository.deleteById(id);
            return ResponseEntity.ok("Question deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deleting question: " + e.getMessage());
        }
    }
}

