package ru.javavlsu.kb.esap.service;

import org.springframework.stereotype.Service;
import ru.javavlsu.kb.esap.dto.notifications.TokenRequest;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.model.TokenStatus;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.model.UserDeviceToken;
import ru.javavlsu.kb.esap.repository.UserDeviceTokenRepository;

@Service
public class UserDeviceTokenService {
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public UserDeviceTokenService(UserDeviceTokenRepository userDeviceTokenRepository) {
        this.userDeviceTokenRepository = userDeviceTokenRepository;
    }

    public void saveToken(User user, TokenRequest request) {
        String tokenValue = request.getToken();

        if (!userDeviceTokenRepository.existsByTokenAndUser(tokenValue, user)) {
            UserDeviceToken deviceToken = new UserDeviceToken();
            deviceToken.setToken(tokenValue);
            deviceToken.setUser(user);
            deviceToken.setStatus(TokenStatus.ACTIVE);
            userDeviceTokenRepository.save(deviceToken);
        }
    }

    public void disableToken(String token) {
        UserDeviceToken deviceToken = userDeviceTokenRepository.findUserDeviceTokenByToken(token)
                .orElseThrow(() -> new NotFoundException("Token=" + token + " not found"));
        deviceToken.setStatus(TokenStatus.INACTIVE);
        userDeviceTokenRepository.save(deviceToken);
    }
}
