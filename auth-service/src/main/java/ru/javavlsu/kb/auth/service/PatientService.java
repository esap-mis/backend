package ru.javavlsu.kb.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.auth.dto.PatientDTO;
import ru.javavlsu.kb.auth.dto.PatientResponseDTO;
import ru.javavlsu.kb.auth.dto.PatientStatisticsByAgeDTO;
import ru.javavlsu.kb.auth.dto.PatientStatisticsByGenderDTO;
import ru.javavlsu.kb.auth.mapper.PatientMapper;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Patient;
import ru.javavlsu.kb.auth.model.RoleName;
import ru.javavlsu.kb.auth.repository.ClinicRepository;
import ru.javavlsu.kb.auth.repository.PatientRepository;
import ru.javavlsu.kb.auth.repository.RoleRepository;
import ru.javavlsu.kb.auth.util.LoginPasswordGenerator;
import ru.javavlsu.kb.common.event.PatientCreatedEvent;
import ru.javavlsu.kb.common.event.UserProfileEvent;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

/**
 * Владеет справочником пациентов. Медкарта живёт в clinic-service и создаётся
 * по событию PatientCreatedEvent, поэтому здесь её больше нет.
 */
@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final ClinicRepository clinicRepository;
    private final PatientMapper patientMapper;
    private final LoginPasswordGenerator lpg;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EventPublisher eventPublisher;

    public PatientService(PatientRepository patientRepository, ClinicRepository clinicRepository,
                          PatientMapper patientMapper, LoginPasswordGenerator lpg, PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository, EventPublisher eventPublisher) {
        this.patientRepository = patientRepository;
        this.clinicRepository = clinicRepository;
        this.patientMapper = patientMapper;
        this.lpg = lpg;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.eventPublisher = eventPublisher;
    }

    public int getPatientCountByClinic(Long clinicId) {
        return patientRepository.countPatientByClinic(requireClinic(clinicId));
    }

    public List<PatientResponseDTO> getLatestPatients(Integer count, Long clinicId) {
        Pageable pageable = PageRequest.of(0, count);
        return patientMapper.toPatientResponseDTOList(
                patientRepository.findAllByClinicOrderByIdDesc(requireClinic(clinicId), pageable).getContent());
    }

    public Page<PatientResponseDTO> getByClinic(String firstName, String patronymic, String lastName,
                                                Long clinicId, int page, int size) {
        return patientMapper.toPatientResponseDTOPage(patientRepository.findAllByFullNameContainingIgnoreCaseAndClinicOrderByIdAsc(
                firstName != null ? firstName : "",
                patronymic != null ? patronymic : "",
                lastName != null ? lastName : "",
                requireClinic(clinicId),
                PageRequest.of(page, size)));
    }

    @Transactional(readOnly = true)
    public Patient getById(long id) {
        return patientRepository.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    public Patient getByLogin(String login) {
        return patientRepository.findByLogin(login).orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    @Transactional
    public Patient create(PatientDTO patientDTO, Long clinicId) {
        Clinic clinic = requireClinic(clinicId);
        Patient patient = patientMapper.toPatient(patientDTO);
        patient.setClinic(clinic);
        patient.setRole(new HashSet<>());
        patient.getRole().add(roleRepository.findByName(RoleName.ROLE_PATIENT)
                .orElseThrow(() -> new NotFoundException("Role not found: " + RoleName.ROLE_PATIENT)));

        String generatedPassword = lpg.generatePassword();
        patient.setLogin(lpg.generateLogin());
        patient.setPassword(passwordEncoder.encode(generatedPassword));

        Patient saved = patientRepository.save(patient);
        saved.setPassword(generatedPassword);

        eventPublisher.sendPatientCreatedEvent(new PatientCreatedEvent(
                saved.getId(), saved.getEmail(), saved.getFirstName(),
                saved.getLogin(), generatedPassword, clinic.getName()));
        eventPublisher.sendUserProfileEvent(UserProfileFactory.from(saved, clinic));
        return saved;
    }

    @Transactional
    public Patient update(Long patientId, PatientDTO patientDTO) {
        Patient patient = getById(patientId);
        patient.setFirstName(patientDTO.firstName());
        patient.setPatronymic(patientDTO.patronymic());
        patient.setLastName(patientDTO.lastName());
        patient.setBirthDate(patientDTO.birthDate());
        patient.setGender(patientDTO.gender());
        patient.setAddress(patientDTO.address());
        patient.setPhoneNumber(patientDTO.phoneNumber());
        patient.setEmail(patientDTO.email());
        Patient saved = patientRepository.save(patient);
        eventPublisher.sendUserProfileEvent(UserProfileFactory.from(saved, patient.getClinic()));
        return saved;
    }

    public PatientStatisticsByGenderDTO getPatientsStatisticsByGender(Long clinicId) {
        Clinic clinic = requireClinic(clinicId);
        return new PatientStatisticsByGenderDTO(
                patientRepository.getPatientsCountByGenderAndClinic(1, clinic),
                patientRepository.getPatientsCountByGenderAndClinic(2, clinic));
    }

    public PatientStatisticsByAgeDTO getPatientsStatisticsByAge(Long clinicId) {
        Clinic clinic = requireClinic(clinicId);
        int today = LocalDate.now().getYear();
        return new PatientStatisticsByAgeDTO(
                patientRepository.countPatientsByAgeRangeAndClinic(
                        LocalDate.of(today - 18, 12, 31), LocalDate.of(today - 100, 1, 1), clinic),
                patientRepository.countPatientsByAgeRangeAndClinic(
                        LocalDate.of(today - 59, 12, 31), LocalDate.of(today - 19, 1, 1), clinic),
                patientRepository.countPatientsByAgeRangeAndClinic(
                        LocalDate.of(today, 12, 31), LocalDate.of(today - 60, 1, 1), clinic));
    }

    public List<PatientResponseDTO> findByFullName(String fullName) {
        List<Patient> patients = patientRepository.findByFullName(fullName);
        if (patients.isEmpty()) {
            throw new NotFoundException("Patient not found");
        }
        return patientMapper.toPatientResponseDTOList(patients);
    }

    private Clinic requireClinic(Long clinicId) {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Clinic not found"));
    }
}
