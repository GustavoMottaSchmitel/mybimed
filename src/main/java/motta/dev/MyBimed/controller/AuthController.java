package motta.dev.MyBimed.controller;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.dto.AuthRequest;
import motta.dev.MyBimed.dto.AuthResponse;
import motta.dev.MyBimed.dto.RegisterRequest;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.UserRepository;
import motta.dev.MyBimed.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        var user = UserModel.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .cargo(request.getCargo()) // Ex: Role.ADMIN ou Role.USER
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(email)
                .orElseThrow();

        if (!jwtService.isTokenValid(refreshToken, user)) {
            return ResponseEntity.status(401).build();
        }

        var accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken) // Mantém o mesmo refresh token
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT é stateless, então geralmente o logout é client-side (apaga o token do lado do cliente)
        return ResponseEntity.ok("Logout realizado com sucesso!");
    }
}