package com.election.service;

import com.election.entity.User;
import com.election.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final EmailParserService emailParserService;
    private final String adminEmail;

    public CustomOAuth2UserService(
            UserRepository userRepository,
            EmailParserService emailParserService,
            @Value("${admin.email}") String adminEmail
    ) {
        this.userRepository = userRepository;
        this.emailParserService = emailParserService;
        this.adminEmail = adminEmail;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null || !email.endsWith("@cmrcet.ac.in")) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_domain",
                    "This platform is only for CMRCET students. Email must end with @cmrcet.ac.in", null)
            );
        }

        if (!(email.equals(adminEmail) || email.startsWith("2"))) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("invalid_email",
                        "This platform is only for Admin and Students of CMRCET.", null)
            );
        }

        String role = email.equalsIgnoreCase(adminEmail) ? "ADMIN" : "STUDENT";

        String rollNumber = null, branch = null, batch = null, studentType = null;
        if (role.equals("STUDENT")) {
            Map<String, String> parsed = emailParserService.parseEmail(email);
            rollNumber = parsed.get("rollNumber");
            branch = parsed.get("branch");
            batch = parsed.get("batch");
            studentType = parsed.get("studentType");
        }

        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setName(name);
        user.setPicture(picture);
        user.setRole(role);
        if (rollNumber != null) {
            user.setRollNumber(rollNumber);
            user.setBranch(branch);
            user.setBatch(batch);
            user.setStudentType(studentType);
        }
        user = userRepository.save(user);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("userId", user.getId());
        attributes.put("role", role);

        return new DefaultOAuth2User(authorities, attributes, "email");
    }
}
