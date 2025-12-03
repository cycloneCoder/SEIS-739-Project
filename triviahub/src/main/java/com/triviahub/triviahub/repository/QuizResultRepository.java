package com.triviahub.triviahub.repository;

import com.triviahub.triviahub.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the QuizResult entity.
 * Handles database operations related to quiz attempts and results.
 */
@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    /**
     * Finds all quiz results for a specific user.
     * @param userId The ID of the user whose results are to be retrieved.
     * @return A list of quiz results.
     */
    List<QuizResult> findByUser_Id(Long userId);
}
