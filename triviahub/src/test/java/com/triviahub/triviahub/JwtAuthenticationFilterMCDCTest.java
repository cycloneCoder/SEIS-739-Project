package com.triviahub.triviahub;

import com.triviahub.triviahub.config.JwtAuthenticationFilter;
import com.triviahub.triviahub.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterMCDCTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * MCDC Decision: (A || B)
     * A = authHeader == null
     * B = !authHeader.startsWith("Bearer ")
     *
     * Test Case 1: (A=True, B=X) -> Outcome = True (Bypasses auth)
     * authHeader is null.
     * Expected: The filter chain continues, and JWT logic is never called.
     */
    @Test
    void doFilterInternal_MCDC_True() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService); // JWT logic is skipped
    }

    /**
     * MCDC Decision: (A || B)
     * Test Case 2: (A=False, B=True) -> Outcome = True (Bypasses auth)
     * authHeader is "Basic ...".
     * Expected: The filter chain continues, and JWT logic is never called.
     *
     * This test, paired with Case 3, proves that B independently affects the outcome.
     */
    @Test
    void doFilterInternal_MCDC_FalseTrue() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic cGFzc3dvcmQ="); // "password"

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService); // JWT logic is skipped
    }

    /**
     * MCDC Decision: (A || B)
     * Test Case 3: (A=False, B=False) -> Outcome = False (Attempts auth)
     * authHeader is "Bearer ...".
     * Expected: The filter chain continues, and JWT logic IS called.
     *
     * This test, paired with Case 1 & 2, proves A and B independently affect the outcome.
     */
    @Test
    void doFilterInternal_MCDC_FalseFalse() throws ServletException, IOException {
        // Arrange
        String validToken = "Bearer foo.bar.baz";
        String username = "testUser";
        when(request.getHeader("Authorization")).thenReturn(validToken);
        // Mock the JWT logic to prevent NullPointers
        when(jwtService.extractUsername("foo.bar.baz")).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(null); // Stop test here

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(jwtService).extractUsername("foo.bar.baz"); // JWT logic was called
    }
}
