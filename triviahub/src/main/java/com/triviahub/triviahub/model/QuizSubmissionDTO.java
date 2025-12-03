package com.triviahub.triviahub.model;

import jakarta.validation.constraints.NotEmpty; 
import jakarta.validation.constraints.NotNull; 
import java.util.Map;

/**
 * Data Transfer Object for submitting a user's answers for a quiz.
 * The user is identified via the JWT token, not included in this DTO.
 */
public class QuizSubmissionDTO {

    @NotNull(message = "Quiz ID cannot be null.")
    private Long quizId;

    /**
     * A map where the key is the question ID (Long) and the value is the user's selected answer (String).
     * Example: { "101": "A", "102": "C", "103": "D" }
     */
    @NotEmpty(message = "Answers map cannot be empty.")
    private Map<Long, String> answers;


    //Getters and Setters

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }
}
