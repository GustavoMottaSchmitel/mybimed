package motta.dev.MyBimed.security.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.enums.Cargo;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.UserRepository;
import motta.dev.MyBimed.security.dto.AuthRequest;
import motta.dev.MyBimed.security.dto.AuthResponse;
import motta.dev.MyBimed.security.dto.MessageResponse;
import motta.dev.MyBimed.security.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = UserModel.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .cargo(Cargo.valueOf(request.getCargo().toUpperCase()))
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(email)
                .orElseThrow();

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Refresh token inválido");
        }

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(refreshToken)
                .build();
    }

    public MessageResponse logout() {
        return MessageResponse.builder()
                .message("Logout realizado com sucesso")
                .build();
    }
}