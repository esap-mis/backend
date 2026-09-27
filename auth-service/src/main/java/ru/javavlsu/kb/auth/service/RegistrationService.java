package ru.javavlsu.kb.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.auth.dto.AuthenticationDTO;
import ru.javavlsu.kb.auth.dto.DoctorRegistration;
import ru.javavlsu.kb.auth.mapper.DoctorMapper;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Doctor;
import ru.javavlsu.kb.auth.model.Role;
import ru.javavlsu.kb.auth.model.RoleName;
import ru.javavlsu.kb.auth.model.User;
import ru.javavlsu.kb.auth.repository.ClinicRepository;
import ru.javavlsu.kb.auth.repository.DoctorRepository;
import ru.javavlsu.kb.auth.repository.RoleRepository;
import ru.javavlsu.kb.auth.repository.UserRepository;
import ru.javavlsu.kb.auth.util.LoginPasswordGenerator;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RegistrationService {

    private final ClinicRepository clinicRepository;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;
    private final RoleRepository roleRepository;
    private final DoctorMapper doctorMapper;
    private final LoginPasswordGenerator lpg;
    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    public RegistrationService(ClinicRepository clinicRepository, PasswordEncoder passwordEncoder,
                               DoctorRepository doctorRepository, RoleRepository roleRepository,
                               DoctorMapper doctorMapper, LoginPasswordGenerator lpg, UserRepository userRepository,
                               EventPublisher eventPublisher) {
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.roleRepository = roleRepository;
        this.doctorMapper = doctorMapper;
        this.lpg = lpg;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String[] registrationClinic(Clinic clinic, Doctor doctor) {
        String login = lpg.generateLogin();
        String password = lpg.generatePassword();
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setClinic(clinic);
        doctor.setRole(new HashSet<>());
        doctor.getRole().add(roleRepository.findByName(RoleName.ROLE_CHIEF_DOCTOR)
                .orElseThrow(() -> new NotFoundException("Role not found")));
        clinicRepository.save(clinic);
        Doctor saved = doctorRepository.save(doctor);
        eventPublisher.sendUserProfileEvent(UserProfileFactory.from(saved, clinic));
        return new String[]{login, password};
    }

    @Transactional
    public String[] registrationDoctor(DoctorRegistration doctorDTO, Long clinicId) {
        String login = lpg.generateLogin();
        String password = lpg.generatePassword();
        Doctor doctor = doctorMapper.toDoctor(doctorDTO, roleRepository);
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setClinic(clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Clinic not found")));
        Doctor saved = doctorRepository.save(doctor);
        eventPublisher.sendUserProfileEvent(UserProfileFactory.from(saved, saved.getClinic()));
        return new String[]{login, password};
    }

    @Transactional
    public void passwordReset(AuthenticationDTO authenticationDTO) {
        User user = userRepository.findByLogin(authenticationDTO.login())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(authenticationDTO.password()));
        userRepository.save(user);
    }

    @Transactional
    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> role.getName().withoutPrefix())
                .collect(Collectors.toList());
    }
}
