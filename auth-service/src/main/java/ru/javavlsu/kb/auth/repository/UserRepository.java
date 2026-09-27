package ru.javavlsu.kb.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.auth.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

}
