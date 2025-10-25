package com.triviahub.triviahub.model;

/**
 * A Data Transfer Object representing a summary of a Quiz.
 * This is used for list views to send only the necessary data to the client,
 * avoiding serialization issues and hiding sensitive information.
 *
 * @param id The ID of the quiz
 * @param title The title of the quiz
 * @param description The description of the quiz
 * @param createdByUsername The username of the user who created the quiz
 */
public record QuizSummaryDTO(
    Long id,
    String title,
    String description,
    String createdByUsername
) {}
