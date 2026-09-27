package ru.javavlsu.kb.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.auth.dto.AuthenticationDTO;
import ru.javavlsu.kb.auth.dto.ClinicRegistrationDTO;
import ru.javavlsu.kb.auth.dto.DoctorRegistration;
import ru.javavlsu.kb.auth.mapper.ClinicMapper;
import ru.javavlsu.kb.auth.mapper.DoctorMapper;
import ru.javavlsu.kb.auth.model.Doctor;
import ru.javavlsu.kb.auth.service.AuthUserDetailsService;
import ru.javavlsu.kb.auth.service.RegistrationService;
import ru.javavlsu.kb.common.security.AuthenticatedUser;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.common.security.JwtTokenProvider;
import ru.javavlsu.kb.common.web.DeviceLoginException;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.common.web.ResponseMessageError;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RegistrationService registrationService;
    private final ClinicMapper clinicMapper;
    private final DoctorMapper doctorMapper;
    private final AuthUserDetailsService authUserDetailsService;
    private final CurrentUser currentUser;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                          RegistrationService registrationService, ClinicMapper clinicMapper, DoctorMapper doctorMapper,
                          AuthUserDetailsService authUserDetailsService, CurrentUser currentUser) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.registrationService = registrationService;
        this.clinicMapper = clinicMapper;
        this.doctorMapper = doctorMapper;
        this.authUserDetailsService = authUserDetailsService;
        this.currentUser = currentUser;
    }

    @PostMapping("/registration/clinic")
    public Map<String, String> registrationClinic(@RequestBody @Valid ClinicRegistrationDTO clinicRegistrationDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        String[] loginPassword = registrationService.registrationClinic(
                clinicMapper.toClinic(clinicRegistrationDTO.clinic()),
                doctorMapper.toDoctor(clinicRegistrationDTO.doctor()));
        return Map.of("login", loginPassword[0], "password", loginPassword[1]);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO, HttpServletRequest request) {
        AuthenticatedUser user = authUserDetailsService.loadAuthenticatedUser(authenticationDTO.login());
        if (user.isPatient()) {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent == null || !userAgent.contains("mobile")) {
                throw new DeviceLoginException("Cannot login from this device");
            }
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDTO.login(), authenticationDTO.password()));
        return Map.of("jwt", jwtTokenProvider.generateToken(user), "roles", String.join(";", user.roles()));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<HttpStatus> passwordReset(@RequestBody AuthenticationDTO authenticationDTO) {
        registrationService.passwordReset(authenticationDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/registration/doctor")
    @PreAuthorize("hasRole('CHIEF_DOCTOR')")
    public Map<String, String> registrationDoctor(@RequestBody @Valid DoctorRegistration doctorRegistration) {
        Long clinicId = currentUser.clinicId();
        if (clinicId == null) {
            throw new NotFoundException("Clinic not found for current user");
        }
        String[] loginPassword = registrationService.registrationDoctor(doctorRegistration, clinicId);
        return Map.of("login", loginPassword[0], "password", loginPassword[1]);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(registrationService.getAllRoles());
    }
}
