package com.brewy.backend.controller;

import com.brewy.backend.model.User;
import com.brewy.backend.model.operations.AuthResponse;
import com.brewy.backend.model.operations.LoginRequest;
import com.brewy.backend.model.operations.RegisterRequest;
import com.brewy.backend.service.AuthService;
import com.brewy.backend.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {
    private final JwtService jwtService;

    private final AuthService authenticationService;

    public AuthController(JwtService jwtService, AuthService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody LoginRequest loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        AuthResponse loginResponse = AuthResponse
                .builder()
                .expiresIn(jwtService.getExpirationTime())
                .token(jwtToken)
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
