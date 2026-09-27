package ru.javavlsu.kb.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.auth.model.User;
import ru.javavlsu.kb.auth.repository.UserRepository;
import ru.javavlsu.kb.common.security.AuthenticatedUser;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Единственный сервис, который умеет аутентифицировать по логину/паролю.
 * Остальные сервисы доверяют подписанному JWT.
 */
@Service
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new AuthenticatedUserDetails(toAuthenticatedUser(user));
    }

    @Transactional(readOnly = true)
    public AuthenticatedUser loadAuthenticatedUser(String login) {
        return toAuthenticatedUser(userRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    private AuthenticatedUser toAuthenticatedUser(User user) {
        Set<String> roles = user.getRole().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        return new AuthenticatedUser(
                user.getId(),
                user.getLogin(),
                roles,
                user.getClinic() != null ? user.getClinic().getId() : null
        );
    }

    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
