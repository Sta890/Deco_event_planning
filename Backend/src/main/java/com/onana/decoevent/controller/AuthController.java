package com.onana.decoevent.controller;
import com.onana.decoevent.dto.response.AuthResponse;
import com.onana.decoevent.dto.request.InscriptionRequest;
import com.onana.decoevent.dto.request.LoginRequest;
import com.onana.decoevent.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.onana.decoevent.exceptions.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/inscription")
    public ResponseEntity<AuthResponse> inscrire(@Valid @RequestBody InscriptionRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.inscrire(request));
    }
}
