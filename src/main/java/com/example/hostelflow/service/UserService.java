package com.example.hostelflow.service;

import com.example.hostelflow.dto.*;
import com.example.hostelflow.mapper.UserMapper;
import com.example.hostelflow.repository.UserRepository;
import com.example.hostelflow.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final SecureRandom secureRandom = new SecureRandom();
    //SecureRandom is Java's cryptographically strong random-number generator.
    //Secure Random is designed for security-sensitive random values,
    //making the generated activation code much harder to predict.

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }
    //adding a student to the database
    public UserProvisionResponse provisionStudent(UserRequest request) {

        if (userRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new IllegalStateException("Registration Number already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        String activationCode = generateActivationCode();
        String activationCodeHash =
                passwordEncoder.encode(activationCode);

        User user = User.forStudent(
                request.getRegistrationNumber(),
                request.getName(),
                request.getEmail(),
                activationCodeHash
        );

        User savedUser = userRepository.save(user);

        return new UserProvisionResponse(userMapper.toResponse(savedUser),activationCode);
    }

    public UserResponse getUserByRegistrationNumber(String registrationNumber) {

        User user = userRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() ->
                        new IllegalStateException("User not found"));

        return userMapper.toResponse(user);
    }
    private String generateActivationCode() {
        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder code = new StringBuilder(9);

        for (int i = 0; i < 9; i++) {
            code.append(
                    characters.charAt(
                            secureRandom.nextInt(characters.length())//67
                    )
            );
        }

        return code.toString();
    }

    @Transactional
    public UserResponse activateStudent(ActivateRequest request) {

        User user = userRepository.findByRegistrationNumber(
                request.getRegistrationNumber()
        ).orElseThrow(() ->
                new IllegalStateException("Invalid activation details"));

        if (user.isActivated()) {
            throw new IllegalStateException("User is already activated");
        }

        // BCrypt verification can't be pushed into a SQL WHERE clause (it's a
        // salted, constant-time comparison done in Java), so this read is
        // still a plain lookup. That's fine — it's not where the race lives.
        if (!passwordEncoder.matches(
                request.getActivationCode(),
                user.getActivationCodeHash())) {
            throw new IllegalStateException("Invalid activation details");
        }

        String passwordHash =
                passwordEncoder.encode(request.getNewPassword());

        int rowsUpdated = userRepository.atomicallyActivate(
                request.getRegistrationNumber(), passwordHash);

        if (rowsUpdated == 0) {
            throw new IllegalStateException("User is already activated");
        }

        User activatedUser = userRepository.findByRegistrationNumber(
                request.getRegistrationNumber()
        ).orElseThrow(() ->
                new IllegalStateException("User not found after activation"));

        return userMapper.toResponse(activatedUser);
    }
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalStateException("Invalid email or password"));

        if (!user.isActivated()) {
            throw new IllegalStateException("Account is not activated");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new IllegalStateException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                token,
                userMapper.toResponse(user)
        );
    }
}
