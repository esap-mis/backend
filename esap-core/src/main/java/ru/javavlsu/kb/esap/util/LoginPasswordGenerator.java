package ru.javavlsu.kb.esap.util;

import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.service.UserService;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class LoginPasswordGenerator {

    private final int PASSWORD_LENGTH = 8;
    private final String PASSWORD_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_-+={}[]:;\"'<>,.?/|\\";

    private final UserService userService;

    public LoginPasswordGenerator(UserService userService) {
        this.userService = userService;
    }

    public String generateLogin() {
        StringBuilder login;
        do {
            login = new StringBuilder();
            String characters = "abcdefghijklmnopqrstuvwxyz";
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                int index = random.nextInt(characters.length());
                login.append(characters.charAt(index));
            }
            login.append(String.format("%04d", new Random().nextInt(10000)));
        } while (userService.userExists(login.toString()));

        return login.toString().toLowerCase();
    }

    public String generatePassword() {
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            password.append(PASSWORD_CHARS.charAt(index));
        }
        return password.toString();
    }

}
