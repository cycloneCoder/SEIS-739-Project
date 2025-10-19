// package com.triviahub.triviahub.model;

// import jakarta.persistence.*;
// import jakarta.validation.constraints.NotBlank;

// @Entity
// public class Question {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @NotBlank(message = "Question text is required")
//     @Column(nullable = false)
//     private String questionText;

//     @NotBlank(message = "Option A is required")
//     @Column(nullable = false)
//     private String optionA;

//     @NotBlank(message = "Option B is required")
//     @Column(nullable = false)
//     private String optionB;

//     private String optionC;
//     private String optionD;

//     @NotBlank(message = "Correct answer is required")
//     @Column(nullable = false)
//     private String correctAnswer; // store correct option (A, B, C, or D)

//     @ManyToOne
//     @JoinColumn(name = "user_id", nullable = false)
//     private User createdBy;

//     public Question() {}

//     public Question(String questionText, String optionA, String optionB,
//                     String optionC, String optionD, String correctAnswer, User createdBy) {
//         this.questionText = questionText;
//         this.optionA = optionA;
//         this.optionB = optionB;
//         this.optionC = optionC;
//         this.optionD = optionD;
//         this.correctAnswer = correctAnswer;
//         this.createdBy = createdBy;
//     }

//     // Getters and setters
//     public Long getId() { return id; }
//     public String getQuestionText() { return questionText; }
//     public void setQuestionText(String questionText) { this.questionText = questionText; }

//     public String getOptionA() { return optionA; }
//     public void setOptionA(String optionA) { this.optionA = optionA; }

//     public String getOptionB() { return optionB; }
//     public void setOptionB(String optionB) { this.optionB = optionB; }

//     public String getOptionC() { return optionC; }
//     public void setOptionC(String optionC) { this.optionC = optionC; }

//     public String getOptionD() { return optionD; }
//     public void setOptionD(String optionD) { this.optionD = optionD; }

//     public String getCorrectAnswer() { return correctAnswer; }
//     public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

//     public User getCreatedBy() { return createdBy; }
//     public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
// }

package com.triviahub.triviahub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a single question entity.
 * Each question is part of a quiz and has multiple-choice options.
 */
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text is required")
    @Column(nullable = false)
    private String questionText;

    @NotBlank(message = "Option A is required")
    @Column(nullable = false)
    private String optionA;

    @NotBlank(message = "Option B is required")
    @Column(nullable = false)
    private String optionB;

    private String optionC;
    private String optionD;

    @NotBlank(message = "Correct answer is required")
    @Column(nullable = false)
    private String correctAnswer; // store correct option (A, B, C, or D)

    /**
     * The quiz this question belongs to.
     * This establishes a many-to-one relationship: many questions can belong to one quiz.
     * The `quiz_id` column in the `question` table will be a foreign key.
     * @JsonIgnore is used to prevent infinite recursion when serializing to JSON (Quiz -> Questions -> Quiz).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    public Question() {}

    public Question(String questionText, String optionA, String optionB,
                    String optionC, String optionD, String correctAnswer, Quiz quiz) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.quiz = quiz;
    }

    // --- Getters and setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
