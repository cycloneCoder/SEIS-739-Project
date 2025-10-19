package com.triviahub.triviahub.model;

import java.util.Map;

/**
 * Data Transfer Object for submitting a user's answers for a quiz.
 */
public class QuizSubmissionDTO {

    private Long userId;
    private Long quizId;

    /**
     * A map where the key is the question ID (Long) and the value is the user's selected answer (String).
     * Example: { "101": "A", "102": "C", "103": "D" }
     */
    private Map<Long, String> answers;


    // --- Getters and Setters ---
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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
