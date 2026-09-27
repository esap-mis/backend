package ru.javavlsu.kb.auth.util;

import org.springframework.stereotype.Component;
import ru.javavlsu.kb.auth.service.AuthUserDetailsService;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class LoginPasswordGenerator {

    private static final int PASSWORD_LENGTH = 8;
    private static final String PASSWORD_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_-+={}[]:;\"'<>,.?/|\\";

    private final AuthUserDetailsService userDetailsService;

    public LoginPasswordGenerator(AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String generateLogin() {
        StringBuilder login;
        do {
            login = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                login.append("abcdefghijklmnopqrstuvwxyz".charAt(random.nextInt(26)));
            }
            login.append(String.format("%04d", random.nextInt(10000)));
        } while (userDetailsService.findByLogin(login.toString()).isPresent());
        return login.toString().toLowerCase();
    }

    public String generatePassword() {
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }
}
