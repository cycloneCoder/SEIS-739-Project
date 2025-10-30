package com.triviahub.triviahub;

import com.triviahub.triviahub.controller.QuizResultController;
import com.triviahub.triviahub.model.*;
import com.triviahub.triviahub.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuizResultControllerMCDCTest {

    @Mock
    private QuizResultRepository quizResultRepository;
    @Mock
    private UserAnswerRepository userAnswerRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuizResultController quizResultController;

    private User testUser;
    private Quiz testQuiz;
    private Question q1;
    private Question q2;
    private Principal principal;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "test@test.com", "pass");
        testUser.setId(1L);

        testQuiz = new Quiz("Test Quiz", "Desc", testUser);
        testQuiz.setId(101L);

        q1 = new Question("Q1", "A", "B", "C", "D", "A", testQuiz);
        q1.setId(1001L);
        q2 = new Question("Q2", "A", "B", "C", "D", "B", testQuiz);
        q2.setId(1002L);

        testQuiz.setQuestions(Arrays.asList(q1, q2));

        principal = () -> "testUser";

        // Mock common repository calls
        when(userRepository.findByUsername("testUser")).thenReturn(testUser);
        when(quizRepository.findById(101L)).thenReturn(Optional.of(testQuiz));
        
        // Mock the save operation to return the object it was given
        when(quizResultRepository.save(any(QuizResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * MCDC Decision: (A && B)
     * A = selectedAnswer != null
     * B = selectedAnswer.equalsIgnoreCase(correctAnswer)
     *
     * Test Case 1: (A=True, B=True) -> Outcome = True (Correct)
     * Q1: Answer "A" (Correct)
     * Q2: Answer "B" (Correct)
     * Expected Score: 2
     */
    @Test
    void submitQuiz_MCDC_TrueTrue() {
        Map<Long, String> answers = new HashMap<>();
        answers.put(1001L, "A"); // T, T
        answers.put(1002L, "B"); // T, T
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setQuizId(101L);
        submission.setAnswers(answers);

        ResponseEntity<?> response = quizResultController.submitQuiz(submission, principal);
        QuizResult result = (QuizResult) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, result.getScore());
    }

    /**
     * MCDC Decision: (A && B)
     * Test Case 2: (A=True, B=False) -> Outcome = False (Incorrect)
     * Q1: Answer "C" (Wrong)
     * Q2: Answer "A" (Wrong)
     * Expected Score: 0
     *
     * This test, paired with Case 1, proves that B independently affects the outcome.
     */
    @Test
    void submitQuiz_MCDC_TrueFalse() {
        Map<Long, String> answers = new HashMap<>();
        answers.put(1001L, "C"); // T, F
        answers.put(1002L, "A"); // T, F
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setQuizId(101L);
        submission.setAnswers(answers);

        ResponseEntity<?> response = quizResultController.submitQuiz(submission, principal);
        QuizResult result = (QuizResult) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, result.getScore());
    }

    /**
     * MCDC Decision: (A && B)
     * Test Case 3: (A=False, B=X) -> Outcome = False (Incorrect)
     * Q1: Answer null (No Answer)
     * Q2: Answer null (No Answer)
     * Expected Score: 0
     *
     * This test, paired with Case 1, proves that A independently affects the outcome.
     */
    @Test
    void submitQuiz_MCDC_False() {
        Map<Long, String> answers = new HashMap<>();
        answers.put(1001L, null); // F, X
        answers.put(1002L, null); // F, X
        QuizSubmissionDTO submission = new QuizSubmissionDTO();
        submission.setQuizId(101L);
        submission.setAnswers(answers);

        ResponseEntity<?> response = quizResultController.submitQuiz(submission, principal);
        QuizResult result = (QuizResult) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, result.getScore());
    }
}
