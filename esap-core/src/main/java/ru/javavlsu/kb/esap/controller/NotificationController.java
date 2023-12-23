package ru.javavlsu.kb.esap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javavlsu.kb.esap.dto.notifications.TokenRequest;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.service.UserDeviceTokenService;
import ru.javavlsu.kb.esap.util.UserUtils;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    private final UserUtils userUtils;
    private final UserDeviceTokenService userDeviceTokenService;

    public NotificationController(UserUtils userUtils, UserDeviceTokenService userDeviceTokenService) {
        this.userUtils = userUtils;
        this.userDeviceTokenService = userDeviceTokenService;
    }

    @PostMapping("/token")
    @PreAuthorize("hasRole('PATIENT') or hasRole('DOCTOR')")
    public ResponseEntity<?> registerToken(@RequestBody TokenRequest request) {
        User user = userUtils.UserDetails().getUser();
        userDeviceTokenService.saveToken(user, request);
        return ResponseEntity.ok("Token registered successfully");
    }
}
