package com.triviahub.triviahub.repository;

import com.triviahub.triviahub.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
