package ru.javavlsu.kb.esap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.repository.UserRepository;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        log.debug("class:UserDetailsService, method:loadUserByUsername, sql:findByLogin");
        Optional<User> user = userRepository.findByLogin(username);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return new ru.javavlsu.kb.esap.security.UserDetails(user.get());
    }

}
