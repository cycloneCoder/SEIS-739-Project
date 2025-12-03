package com.triviahub.triviahub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents the result of a single attempt by a user to take a quiz.
 * It stores the user, the quiz taken, the final score, and when it was completed.
 */
@Entity
public class QuizResult {

    /**
     * The unique identifier for this quiz attempt record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user who took the quiz.
     * Establishes a many-to-one relationship: a user can have many quiz results.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * The quiz that was taken.
     * Establishes a many-to-one relationship: a quiz can have many results from different users.
     */
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    //@JsonIgnore
    private Quiz quiz;

    /**
     * The final score achieved by the user in this attempt.
     * This could be the number of correct answers or a percentage.
     */
    @Column(nullable = false)
    private int score;

    /**
     * The timestamp of when the quiz was completed.
     */
    @Column(nullable = false)
    private LocalDateTime completedAt;

    /**
     * A list of the specific answers the user gave during this quiz attempt.
     * This provides a detailed breakdown of the result.
     */
    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore //Added this to remove duplicate from JSON returned using the results endpoint
    private List<UserAnswer> userAnswers;

    public QuizResult() {}

    public QuizResult(User user, Quiz quiz, int score) {
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.completedAt = LocalDateTime.now(); // Set completion time on creation
    }

    //Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }
}