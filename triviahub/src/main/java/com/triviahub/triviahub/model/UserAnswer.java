package com.triviahub.triviahub.model;

import jakarta.persistence.*;

/**
 * Represents a user's specific answer to a single question within a quiz attempt.
 * This allows for detailed tracking of which questions were answered correctly or incorrectly.
 */
@Entity
public class UserAnswer {

    /**
     * The unique identifier for this specific answer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The overall quiz result this answer belongs to.
     * Establishes a many-to-one relationship: many answers are part of one quiz result.
     */
    @ManyToOne
    @JoinColumn(name = "quiz_result_id", nullable = false)
    private QuizResult quizResult;

    /**
     * The question that was answered.
     * Establishes a many-to-one relationship: a question can be answered many times across different quiz results.
     */
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * The answer selected by the user (e.g., "A", "B", "C", or "D").
     */
    @Column(nullable = false)
    private String selectedAnswer;

    // --- Constructors ---

    public UserAnswer() {}

    public UserAnswer(QuizResult quizResult, Question question, String selectedAnswer) {
        this.quizResult = quizResult;
        this.question = question;
        this.selectedAnswer = selectedAnswer;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizResult getQuizResult() {
        return quizResult;
    }

    public void setQuizResult(QuizResult quizResult) {
        this.quizResult = quizResult;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }
}