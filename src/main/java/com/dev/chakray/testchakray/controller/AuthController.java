package com.dev.chakray.testchakray.controller;

import com.dev.chakray.testchakray.model.Response;
import com.dev.chakray.testchakray.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dev.chakray.testchakray.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody User user) {

        Response response = new Response();

        try {
            User loggedUser = userService.login(user.getTax_id(), user.getPassword());

            response.setSuccess(true);
            response.setData(loggedUser);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(e.getMessage());

            if (e.getMessage().equals("User not found"))
                return ResponseEntity.status(404).body(response);

            if (e.getMessage().equals("Invalid credentials"))
                return ResponseEntity.status(401).body(response);

            return ResponseEntity.badRequest().body(response);
        }
    }
}
