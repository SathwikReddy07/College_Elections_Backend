package com.election.service;

import com.election.dto.AuthResponse;
import com.election.entity.User;
import com.election.repository.UserRepository;
import com.election.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse authenticate(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User oAuth2User)) {
            throw new RuntimeException("Authentication failed: User is not authenticated via OAuth2");
        }

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String role = oAuth2User.getAttribute("role");
        Long userId = oAuth2User.getAttribute("userId");

        if (userId == null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
            userId = user.getId();
            role = user.getRole();
        }

        String jwt = jwtTokenProvider.generateToken(email, role, userId);

        return new AuthResponse(jwt, email, name, picture, role);
    }
}
