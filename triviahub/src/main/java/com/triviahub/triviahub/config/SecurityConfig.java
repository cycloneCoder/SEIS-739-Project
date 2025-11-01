// package com.triviahub.triviahub.config;

// import com.triviahub.triviahub.repository.UserRepository;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// /**
//  * Main security configuration class for the application.
//  */
// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final JwtAuthenticationFilter jwtAuthFilter;

//     public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
//         this.jwtAuthFilter = jwtAuthFilter;
//     }

//     /**
//      * Configures the security filter chain. This is the main entry point for security rules.
//      */
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable()) // Disable CSRF as we are using JWT for a stateless API
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/api/auth/**").permitAll() // Allow access to login/register endpoints
//                 .anyRequest().authenticated() // All other requests must be authenticated
//             )
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
//             .authenticationProvider(authenticationProvider())
//             .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter

//         return http.build();
//     }

//     /**
//      * Provides the user details service to Spring Security.
//      * It fetches user details from the database using the UserRepository.
//      */
//     @Bean
//     public UserDetailsService userDetailsService(UserRepository userRepository) {
//         return username -> userRepository.findByUsername(username);
//     }

//     /**
//      * Defines the authentication provider, which uses our custom UserDetailsService and password encoder.
//      */
//     @Bean
//     public AuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService(null)); // Will be injected
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     /**
//      * Exposes the AuthenticationManager as a Bean.
//      */
//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }

//     /**
//      * Defines the password encoder. We use BCrypt for strong, salted password hashing.
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }

package com.triviahub.triviahub.config;

import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main security configuration class for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // The filter is no longer injected into the constructor, breaking the cycle.

    /**
     * Configures the security filter chain. This is the main entry point for security rules.
     * Dependencies like the filter and provider are injected directly into this method.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF as we are using JWT for a stateless API
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**"
                ).permitAll() // Allow access to login/register endpoints
                .anyRequest().authenticated() // All other requests must be authenticated
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
            .authenticationProvider(authenticationProvider) // Use the injected provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter

        return http.build();
    }

    /**
     * Provides the user details service to Spring Security.
     * It fetches user details from the database using the UserRepository.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            return user;
        };
    }

    /**
     * Defines the authentication provider, which uses our custom UserDetailsService and password encoder.
     * Dependencies are passed in as parameters by Spring.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Set the user details service
        authProvider.setPasswordEncoder(passwordEncoder); // Set the password encoder
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager as a Bean.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the password encoder. We use BCrypt for strong, salted password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
