package ru.javavlsu.kb.esap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.Role;
import ru.javavlsu.kb.esap.repository.UserRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(String login){
        log.debug("class:UserService, method:userExists, sql:findByLogin");
        return userRepository.findByLogin(login).isPresent();
    }

    public List<String> getRoles(String login){
        log.debug("class:UserService, method:getRoles, sql:findByLogin");
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getRole().stream().map(Role::getName).toList();
    }

}
