package ru.javavlsu.kb.esap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.model.UserDeviceToken;

import java.util.List;
import java.util.Optional;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {

    List<UserDeviceToken> getUserDeviceTokensByUser(User user);

    boolean existsByTokenAndUser(String token, User user);

    Optional<UserDeviceToken> findUserDeviceTokenByToken(String token);
}
