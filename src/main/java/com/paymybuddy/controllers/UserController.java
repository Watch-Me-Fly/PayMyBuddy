package com.paymybuddy.controllers;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create ____________________________________
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestParam User user) {

        userService.signup(user.getUsername(), user.getEmail(), user.getPassword());
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.CREATED).body("User account created");

        return response;
    }
    @PutMapping("/addConnection")
    public ResponseEntity<String> addConnection(@RequestParam Integer userId, @RequestParam Integer connectionId) {

        if (userId == null || connectionId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (userId.equals(connectionId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (userService.checkUserExists(userId) && userService.checkUserExists(connectionId)) {
            userService.addConnection(userId, connectionId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Connection created");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("One or both users is/are not found");
        }
    }
    // read ______________________________________
    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable int id) {
        return userService.findById(id);
    }

    @GetMapping("/{username}")
    public Optional<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/{email}")
    public Optional<User> findByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/{id}/connections")
    public Set<User> getAllConnections(@PathVariable int id) {
        return userService.getAllConnections(id);
    }
    // update ____________________________________
    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody User user) {

        if (userService.checkUserExists(user.getId())) {
            userService.updateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body("User updated");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    // delete ____________________________________
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {

        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            userService.deleteUser(user.get());
            return ResponseEntity.status(HttpStatus.OK).body("User deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    @DeleteMapping("/{username1}/{username2}")
    public ResponseEntity<String> deleteConnection(@PathVariable String username1, @PathVariable String username2) {

        Optional<User> user = userService.findByUsername(username1);
        Optional<User> user2 = userService.findByUsername(username2);

        if (user.isPresent() && user2.isPresent()) {
            userService.removeConnection(user.get().getId(), user2.get().getId());
            return ResponseEntity.status(HttpStatus.OK).body("Connection removed");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("One or both users not found, connection could not be removed");
        }
    }

}
