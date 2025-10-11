package com.triviahub.triviahub.controller;

import com.triviahub.triviahub.model.Question;
import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.QuestionRepository;
import com.triviahub.triviahub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    // Create question (associate with a user)
    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody Question question, @RequestParam("userId") Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        question.setCreatedBy(userOpt.get());
        Question savedQuestion = questionRepository.save(question);
        return ResponseEntity.ok(savedQuestion);
    }

    // Get all questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Get specific question
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        return questionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update question
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id, @RequestBody Question updatedQuestion) {
        return questionRepository.findById(id).map(existing -> {
            existing.setQuestionText(updatedQuestion.getQuestionText());
            existing.setOptionA(updatedQuestion.getOptionA());
            existing.setOptionB(updatedQuestion.getOptionB());
            existing.setOptionC(updatedQuestion.getOptionC());
            existing.setOptionD(updatedQuestion.getOptionD());
            existing.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
            questionRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete question
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        if (!questionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        questionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

