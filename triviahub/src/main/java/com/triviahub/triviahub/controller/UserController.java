// package com.triviahub.triviahub.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.triviahub.triviahub.model.User;
// import com.triviahub.triviahub.repository.UserRepository;

// import jakarta.validation.Valid;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/users")
// public class UserController {

//     @Autowired
//     private UserRepository userRepository;

//     // ✅ CREATE
//     @PostMapping
//     public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
//         try {
//             User newUser = userRepository.save(user);
//             return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
//         } catch (DataIntegrityViolationException e) {
//             // Duplicate email or username (violating unique constraint)
//             return ResponseEntity.status(HttpStatus.CONFLICT)
//                     .body("Email or username already exists. Please use different credentials.");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body("An error occurred while creating the user.");
//         }
//     }

//     // ✅ READ - Get all users
//     @GetMapping
//     public ResponseEntity<List<User>> getAllUsers() {
//         List<User> users = userRepository.findAll();
//         return ResponseEntity.ok(users);
//     }

//     // ✅ READ - Get user by ID
//     @GetMapping("/{id}")
//     public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
//         Optional<User> user = userRepository.findById(id);
        
//         if(user.isPresent()) {
//             return ResponseEntity.ok(user.get());
//         } else {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                                 .body("User not found with ID: " + id);
//         }
        
        
//         // return user.map(ResponseEntity::ok)
//         //         .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//         //                 .body("User not found with ID: " + id));
//     }


    

//     // ✅ UPDATE
//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser) {
//         Optional<User> existingUserOpt = userRepository.findById(id);

//         if (existingUserOpt.isEmpty()) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body("User not found with ID: " + id);
//         }

//         try {
//             User existingUser = existingUserOpt.get();
//             existingUser.setUsername(updatedUser.getUsername());
//             existingUser.setEmail(updatedUser.getEmail());
//             existingUser.setPassword(updatedUser.getPassword());
//             User savedUser = userRepository.save(existingUser);

//             return ResponseEntity.ok(savedUser);

//         } catch (DataIntegrityViolationException e) {
//             return ResponseEntity.status(HttpStatus.CONFLICT)
//                     .body("Email or username already exists. Please use different credentials.");
//         }
//     }

//     // ✅ DELETE
//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
//         if (!userRepository.existsById(id)) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body("User not found with ID: " + id);
//         }

//         userRepository.deleteById(id);
//         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//     }
// }


package com.triviahub.triviahub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.triviahub.triviahub.model.User;
import com.triviahub.triviahub.repository.UserRepository;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * DEPRECATED: This method of user creation is insecure as it does not hash the password.
     * The correct way to create a user is via the '/api/auth/register' endpoint.
     * This endpoint is now removed to prevent accidental use.
     * If you needed an admin to create users, you would inject the PasswordEncoder here.
     */
    /*
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        // This is INSECURE. The AuthController should be used for registration.
        // Passwords must be hashed.
        try {
            User newUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email or username already exists. Please use different credentials.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the user.");
        }
    }
    */

    /**
     * READ - Get all users.
     * This is an administrative endpoint and requires authentication.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * READ - Get user by ID.
     * This is an administrative endpoint and requires authentication.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        
        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("User not found with ID: " + id);
        }
    }

    /**
     * UPDATE - Update a user's details.
     * Note: This method should be used with caution, especially regarding passwords.
     * A dedicated "change password" endpoint would be more secure.
     * This requires authentication.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);

        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }

        try {
            User existingUser = existingUserOpt.get();
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            // WARNING: Directly setting password here bypasses hashing.
            // For a real app, you'd handle password updates separately and securely.
            // existingUser.setPassword(updatedUser.getPassword()); 
            User savedUser = userRepository.save(existingUser);

            return ResponseEntity.ok(savedUser);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email or username already exists. Please use different credentials.");
        }
    }

    /**
     * DELETE - Delete a user by ID.
     * This is an administrative endpoint and requires authentication.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
