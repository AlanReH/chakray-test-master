package com.dev.chakray.testchakray.controller;

import com.dev.chakray.testchakray.model.Response;
import com.dev.chakray.testchakray.model.User;
import com.dev.chakray.testchakray.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String filter
    ) {

        Response response = new Response();

        try {
            List<User> users = userService.getUsers(sortedBy, filter);

            response.setSuccess(true);
            response.setData(users);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.setSuccess(false);
            response.setData(e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody User user) {

        Response response = new Response();

        try {
            User createdUser = userService.createUser(user);

            response.setSuccess(true);
            response.setData(createdUser);

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response> updateUser(
            @PathVariable String userId,
            @RequestBody User user) {

        Response response = new Response();

        try {
            User updatedUser = userService.updateUser(userId, user);

            response.setSuccess(true);
            response.setData(updatedUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(e.getMessage());

            if (e.getMessage().equals("User not found"))
                return ResponseEntity.status(404).body(response);

            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response> deleteUser(@PathVariable String userId) {

        Response response = new Response();

        try {
            userService.deleteUser(userId);

            response.setSuccess(true);
            response.setData("User deleted");

            return ResponseEntity.status(200).body(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(e.getMessage());

            if (e.getMessage().equals("User not found"))
                return ResponseEntity.status(404).body(response);

            return ResponseEntity.badRequest().body(response);
        }
    }
}