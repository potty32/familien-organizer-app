package com.familienorganizer.controller;

import com.familienorganizer.dto.SessionRequest;
import com.familienorganizer.dto.UserResponse;
import com.familienorganizer.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {

    private final UserService userService;

    @PostMapping
    public UserResponse selectProfile(@Valid @RequestBody SessionRequest request) {
        return userService.selectProfile(request);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearSession() {
        // Session-State liegt im Angular-Frontend (Signal) – Backend bestätigt nur
        return ResponseEntity.noContent().build();
    }
}
