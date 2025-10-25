// package com.triviahub.triviahub.repository;

// import com.triviahub.triviahub.model.Quiz;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// /**
//  * Spring Data JPA repository for the Quiz entity.
//  * This interface handles all database operations for quizzes.
//  */
// @Repository
// public interface QuizRepository extends JpaRepository<Quiz, Long> {

//     /**
//      * Finds all quizzes created by a specific user, identified by their ID.
//      * @param userId The ID of the user.
//      * @return A list of quizzes created by the specified user.
//      */
//     List<Quiz> findByCreatedBy_Id(Long userId);
// }

package com.triviahub.triviahub.repository;

import com.triviahub.triviahub.model.Quiz;
import com.triviahub.triviahub.model.QuizSummaryDTO; // Import the new DTO
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Quiz entity.
 * This interface handles all database operations for quizzes.
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    /**
     * Finds all quizzes created by a specific user, identified by their ID.
     * @param userId The ID of the user.
     * @return A list of quizzes created by the specified user.
     */
    List<Quiz> findByCreatedBy_Id(Long userId);

    /**
     * Finds all quizzes and returns them as a simplified DTO.
     * This query uses a "constructor expression" to build the DTOs directly
     * from the database query, which is highly efficient.
     * It joins with the createdBy user to fetch the username.
     *
     * @return A list of QuizSummaryDTO objects.
     */
    @Query("SELECT new com.triviahub.triviahub.model.QuizSummaryDTO(q.id, q.title, q.description, u.username) " +
           "FROM Quiz q JOIN q.createdBy u")
    List<QuizSummaryDTO> findAllQuizSummaries();
}