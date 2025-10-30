package com.triviahub.triviahub;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.triviahub.triviahub.service.JwtService;

import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * MCDC Test for the decision in JwtService.isTokenValid():
 * Decision: (A && B)
 * A = username.equals(userDetails.getUsername())
 * B = !isTokenExpired(token)
 */
@ExtendWith(MockitoExtension.class)
public class JwtServiceMCDCTest {

    @Mock
    private UserDetails mockUserDetails;

    @Spy // We use @Spy to call the real isTokenValid method but mock its internal dependencies
    private JwtService jwtService;

    private static final String DUMMY_TOKEN = "test-token";
    private static final String TEST_USERNAME = "testUser";

    @BeforeEach
    void setUp() {
        // Configure the mock UserDetails to always expect "testUser"
        when(mockUserDetails.getUsername()).thenReturn(TEST_USERNAME);
    }

    /**
     * MCDC Test Case 1: (A=True, B=True) -> Outcome = True
     * - A (Username matches): True
     * - B (Token is NOT expired): True
     * Expected: isTokenValid returns true
     */
    @Test
    void isTokenValid_MCDC_TrueTrue_ReturnsTrue() {
        // Arrange
        // Condition A (Username) = True
        doReturn(TEST_USERNAME).when(jwtService).extractUsername(DUMMY_TOKEN);
        
        // Condition B (!isTokenExpired) = True
        // We make extractExpiration return a future date, so isTokenExpired returns false
        Date futureDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        // We must mock extractClaim, as that's what extractExpiration calls
        doReturn(futureDate).when(jwtService).extractClaim(eq(DUMMY_TOKEN), any(Function.class));

        // Act
        boolean isValid = jwtService.isTokenValid(DUMMY_TOKEN, mockUserDetails);

        // Assert
        assertTrue(isValid);
    }

    /**
     * MCDC Test Case 2: (A=True, B=False) -> Outcome = False
     * - A (Username matches): True
     * - B (Token is NOT expired): False (i.e., token IS expired)
     * Expected: isTokenValid returns false
     *
     * This test, paired with Case 1, proves Condition B independently affects the outcome.
     */
    @Test
    void isTokenValid_MCDC_TrueFalse_ReturnsFalse() {
        // Arrange
        // Condition A (Username) = True
        doReturn(TEST_USERNAME).when(jwtService).extractUsername(DUMMY_TOKEN);

        // Condition B (!isTokenExpired) = False
        // We make extractExpiration return a past date, so isTokenExpired returns true
        Date pastDate = new Date(System.currentTimeMillis() - 1000 * 60 * 60);
        // We must mock extractClaim, as that's what extractExpiration calls
        doReturn(pastDate).when(jwtService).extractClaim(eq(DUMMY_TOKEN), any(Function.class));

        // Act
        boolean isValid = jwtService.isTokenValid(DUMMY_TOKEN, mockUserDetails);

        // Assert
        assertFalse(isValid);
    }

    /**
     * MCDC Test Case 3: (A=False, B=True) -> Outcome = False
     * - A (Username matches): False
     * - B (Token is NOT expired): True
     * Expected: isTokenValid returns false
     *
     * This test, paired with Case 1, proves Condition A independently affects the outcome.
     */
    @Test
    void isTokenValid_MCDC_FalseTrue_ReturnsFalse() {
        // Arrange
        // Condition A (Username) = False
        doReturn("wrongUser").when(jwtService).extractUsername(DUMMY_TOKEN);

        // Condition B (!isTokenExpired) is not evaluated due to short-circuiting.
        // We don't need to mock extractClaim.

        // Act
        boolean isValid = jwtService.isTokenValid(DUMMY_TOKEN, mockUserDetails);

        // Assert
        assertFalse(isValid);
        
        // We can also verify that the second condition was never checked
        verify(jwtService, never()).extractClaim(any(), any());
    }
}