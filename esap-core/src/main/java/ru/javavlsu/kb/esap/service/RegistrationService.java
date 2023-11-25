package ru.javavlsu.kb.esap.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.auth.AuthenticationDTO;
import ru.javavlsu.kb.esap.dto.auth.DoctorRegistration;
import ru.javavlsu.kb.esap.mapper.DoctorMapper;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.Role;
import ru.javavlsu.kb.esap.model.User;
import ru.javavlsu.kb.esap.repository.ClinicRepository;
import ru.javavlsu.kb.esap.repository.DoctorRepository;
import ru.javavlsu.kb.esap.repository.RoleRepository;
import ru.javavlsu.kb.esap.repository.UserRepository;
import ru.javavlsu.kb.esap.util.LoginPasswordGenerator;
import ru.javavlsu.kb.esap.exception.NotFoundException;

import java.util.*;
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

    public RegistrationService(ClinicRepository clinicRepository, PasswordEncoder passwordEncoder, DoctorRepository doctorRepository, RoleRepository roleRepository, DoctorMapper doctorMapper, LoginPasswordGenerator lpg, UserRepository userRepository) {
        this.clinicRepository = clinicRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.roleRepository = roleRepository;
        this.doctorMapper = doctorMapper;
        this.lpg = lpg;
    }

    @Transactional
    public String[] registrationClinic(Clinic clinic, Doctor doctor) throws NotFoundException {
        String login = lpg.generateLogin();
        String password = lpg.generatePassword();
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setClinic(clinic);
        doctor.setRole(new HashSet<>());
        doctor.getRole().add(roleRepository.findByName("ROLE_CHIEF_DOCTOR")
                .orElseThrow(() -> new NotFoundException("Role not found")));
        clinic.setUsers(Collections.singletonList(doctor));
        clinicRepository.save(clinic);
        return new String[]{login, password};
    }

    @Transactional
    public String[] registrationDoctor(DoctorRegistration doctorDTO, Clinic clinic) throws NotFoundException {
        String login = lpg.generateLogin();
        String password = lpg.generatePassword();
        Doctor doctor = doctorMapper.toDoctor(doctorDTO);
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setClinic(clinic);
        doctor.setRole(new HashSet<>());
        doctor.getRole().add(roleRepository.findByName("ROLE_" + doctorDTO.getRole())
                .orElseThrow(() -> new NotFoundException("Role not found")));
        doctorRepository.save(doctor);
        return new String[] {login, password};
    }

    @Transactional
    public void passwordReset(AuthenticationDTO authenticationDTO) {
        User user = userRepository.findByLogin(authenticationDTO.getLogin())
                .orElseThrow(() -> new NotFoundException("Doctor not found"));

        user.setPassword(passwordEncoder.encode(authenticationDTO.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public List<String> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role -> role.getName().replace("ROLE_", ""))
                .collect(Collectors.toList());
    }
}
