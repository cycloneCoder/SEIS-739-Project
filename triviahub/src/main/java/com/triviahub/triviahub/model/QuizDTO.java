// package com.triviahub.triviahub.model;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

// /**
//  * Data Transfer Object for creating a new Quiz.
//  * It carries the necessary data from the client to the server.
//  */
// public class QuizDTO {

//     @NotBlank(message = "Quiz title cannot be blank. ")
//     @Size(min =3, message = "Title must be between 3 and 100 characters.")
//     private String title;
//     private String description;

    
//     private Long userId; // The ID of the user creating the quiz

//     // --- Getters and Setters ---
//     public String getTitle() {
//         return title;
//     }

//     public void setTitle(String title) {
//         this.title = title;
//     }

//     public String getDescription() {
//         return description;
//     }

//     public void setDescription(String description) {
//         this.description = description;
//     }

//     public Long getUserId() {
//         return userId;
//     }

//     public void setUserId(Long userId) {
//         this.userId = userId;
//     }
// }


package com.triviahub.triviahub.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating a new Quiz.
 * It carries the necessary data from the client to the server.
 * Note: It does not contain a userId, as the user is identified via the JWT in the security context.
 */
public class QuizDTO {

    @NotBlank(message = "Quiz title cannot be blank.")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters.")
    private String title;

    private String description;

    // --- Getters and Setters ---
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
