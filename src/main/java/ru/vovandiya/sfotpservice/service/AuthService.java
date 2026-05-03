package ru.vovandiya.sfotpservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vovandiya.sfotpservice.dto.AuthResponse;
import ru.vovandiya.sfotpservice.dto.LoginRequest;
import ru.vovandiya.sfotpservice.dto.RegisterRequest;
import ru.vovandiya.sfotpservice.model.Role;
import ru.vovandiya.sfotpservice.model.User;
import ru.vovandiya.sfotpservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        if (request.getRole() == Role.ADMIN && userRepository.existsByRole(Role.ADMIN)) {
            throw new IllegalArgumentException("Admin already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);

        return new AuthResponse(jwtService.generate(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return new AuthResponse(jwtService.generate(user));
    }
}