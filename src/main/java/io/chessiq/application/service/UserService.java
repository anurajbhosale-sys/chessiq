package io.chessiq.application.service;

import io.chessiq.api.dto.request.LoginRequest;
import io.chessiq.api.dto.request.RegisterUserRequest;
import io.chessiq.api.dto.response.LoginResponse;
import io.chessiq.api.dto.response.UserResponse;
import io.chessiq.domain.exception.EmailAlreadyExistsException;
import io.chessiq.domain.exception.InvalidCredentialsException;
import io.chessiq.infrastructure.persistence.entity.UserEntity;
import io.chessiq.infrastructure.persistence.repository.UserRepository;
import io.chessiq.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        UserEntity user = new UserEntity(
                request.email(),
                passwordEncoder.encode(request.password())
        );
        UserEntity saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getCreatedAt());
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return new LoginResponse(jwtService.generateToken(user.getId()));
    }
}