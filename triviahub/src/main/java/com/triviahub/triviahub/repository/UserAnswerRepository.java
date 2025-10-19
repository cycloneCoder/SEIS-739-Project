package com.triviahub.triviahub.repository;

import com.triviahub.triviahub.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAnswer entity.
 * Manages the storage of individual user answers for quiz attempts.
 */
@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
}
