// package com.triviahub.triviahub.model;


// import jakarta.persistence.*;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

// @Entity
// @Table(name = "users") // "user" is a reserved word in PostgreSQL, so use "users"
// public class User {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
//     private Long id;

//     @NotBlank(message = "Username is required")
//     @Column(nullable = false, unique = true)
//     private String username;

//     @Email(message = "Email should be valid")
//     @NotBlank(message = "Email is required")
//     @Column(nullable = false, unique = true)
//     private String email;

//     @NotBlank(message = "Password is required")
//     @Size(min = 6, message = "Password must be at least 6 characters long")
//     @Column(nullable = false)
//     private String password;

//     // Constructors
//     public User() {}

//     public User(String username, String email, String password) {
//         this.username = username;
//         this.email = email;
//         this.password = password;
//     }

//     // Getters & setters
//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }
// }


package com.triviahub.triviahub.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users") // "user" is a reserved word in PostgreSQL, so use "users"
public class User implements UserDetails { // Implement the UserDetails interface

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(nullable = false)
    private String password;

    // Constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters & setters for your fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // --- UserDetails Interface Methods ---

    /**
     * Returns the authorities granted to the user. For this simple app, we return an empty list.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * The account is never expired.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * The account is never locked.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * The credentials are never expired.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * The user is always enabled.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}