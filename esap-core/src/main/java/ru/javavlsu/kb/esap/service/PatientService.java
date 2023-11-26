package ru.javavlsu.kb.esap.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.esap.dto.PatientDTO;
import ru.javavlsu.kb.esap.dto.PatientStatisticsByAgeDTO;
import ru.javavlsu.kb.esap.dto.PatientStatisticsByGenderDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.mapper.PatientMapper;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.MedicalCard;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.repository.MedicalCardRepository;
import ru.javavlsu.kb.esap.repository.PatientRepository;
import ru.javavlsu.kb.esap.exception.NotFoundException;
import ru.javavlsu.kb.esap.repository.RoleRepository;
import ru.javavlsu.kb.esap.util.LoginPasswordGenerator;
import ru.javavlsu.kb.esap.util.PatientWithPassword;

import java.util.HashSet;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final MedicalCardRepository medicalCardRepository;
    private final PatientMapper patientMapper;
    private final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final LoginPasswordGenerator lpg;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public PatientService(PatientRepository patientRepository, MedicalCardRepository medicalCardRepository, PatientMapper patientMapper, LoginPasswordGenerator lpg, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.medicalCardRepository = medicalCardRepository;
        this.patientMapper = patientMapper;
        this.patientRepository = patientRepository;
        this.lpg = lpg;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<PatientResponseDTO> getAll() {
        List<Patient> patients = patientRepository.findAll();
        return patientMapper.toPatientResponseDTOList(patients);
    }

    public int getPatientCountByClinic(Clinic clinic) {
        return patientRepository.countPatientByClinic(clinic);
    }

    public List<PatientResponseDTO> getLatestPatients(Integer count, Clinic clinic) {
        Pageable pageable = PageRequest.of(0, count);
        log.debug("class:PatientService, method:getLatestPatients, sql:findAllByClinicOrderByIdDesc");
        List<Patient> patients = patientRepository.findAllByClinicOrderByIdDesc(clinic, pageable).stream().toList();
        return patientMapper.toPatientResponseDTOList(patients);
    }

    public Page<PatientResponseDTO> getByClinic(String firstName, String patronymic, String lastName, Clinic clinic, int page, int size) {
        log.debug("class:PatientService, method:getByClinic, sql:findAllByFullNameContainingIgnoreCaseAndClinicOrderByIdAsc");
        Page<Patient> patients = patientRepository.findAllByFullNameContainingIgnoreCaseAndClinicOrderByIdAsc(
                firstName != null ? firstName : "",
                patronymic != null ? patronymic : "",
                lastName != null ? lastName : "",
                clinic,
                PageRequest.of(page, size)
        );
        return patientMapper.toPatientResponseDTOPage(patients);
    }

    @Transactional(readOnly = true)
    public Patient getById(long id) {
        log.debug("class:PatientService, method:getById, sql:findById");
        return patientRepository.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    @Transactional
    public PatientWithPassword create(PatientDTO patientDTO, Clinic clinic) {
        Patient patient = patientMapper.toPatient(patientDTO);
        patient.setClinic(clinic);
        patient.setMedicalCard(new MedicalCard(patient));
        log.debug("class:PatientService, method:create, sql:save");
        patient.setRole(new HashSet<>());
        String role = "ROLE_PATIENT";
        patient.getRole().add(roleRepository.findByName(role)
                .orElseThrow(() -> new NotFoundException("Role not found: " + role)));
        String generatedPassword = lpg.generatePassword();
        String generatedLogin = lpg.generateLogin();
        patient.setPassword(passwordEncoder.encode(generatedPassword));
        patient.setLogin(generatedLogin);
        patientRepository.save(patient);
//        TODO FOR TESTS
//        Patient patientCreate = patientRepository.save(patient);
//        patientCreate.setLogin("00" + patientCreate.getId().toString());
//        patientCreate.setPassword(passwordEncoder.encode("123"));
//        return patientRepository.save(patientCreate);
        return new PatientWithPassword(patient, generatedPassword);
    }

    @Transactional
    public Patient update(Long patientId, PatientDTO patientDTO) {
        log.debug("class:PatientService, method:update, sql:findById");
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient with id=" + patientId + " not found"));
        patient.setFirstName(patientDTO.getFirstName());
        patient.setPatronymic(patientDTO.getPatronymic());
        patient.setLastName(patientDTO.getLastName());
        patient.setBirthDate(patientDTO.getBirthDate());
        patient.setGender(patientDTO.getGender());
        patient.setAddress(patientDTO.getAddress());
        patient.setPhoneNumber(patientDTO.getPhoneNumber());
        patient.setEmail(patientDTO.getEmail());
        log.debug("class:PatientService, method:update, sql:save");
        return patientRepository.save(patient);
    }

    public PatientStatisticsByGenderDTO getPatientsStatisticsByGender(Clinic clinic) {
        long c = clinic.getId();
        log.debug("class:PatientService, method:getPatientsStatisticsByGender, sql:getPatientsCountByGenderAndClinic. x2");
        int malePatients = patientRepository.getPatientsCountByGenderAndClinic(1, clinic);
        int femalePatients = patientRepository.getPatientsCountByGenderAndClinic(2, clinic);

        PatientStatisticsByGenderDTO statistics = new PatientStatisticsByGenderDTO();
        statistics.setMale(malePatients);
        statistics.setFemale(femalePatients);

        return statistics;
    }

    @Transactional(readOnly = true)
    public PatientStatisticsByAgeDTO getPatientsStatisticsByAge(Clinic clinic) {
        log.debug("class:PatientService, method:getPatientsStatisticsByAge, sql:countPatientsByAgeRangeAndClinic. x3");
        int childCount = patientRepository.countPatientsByAgeRangeAndClinic(0, 18, clinic);
        int adultCount = patientRepository.countPatientsByAgeRangeAndClinic(19, 59, clinic);
        int elderlyCount = patientRepository.countPatientsByAgeRangeAndClinic(60, 100, clinic);

        PatientStatisticsByAgeDTO statistics = new PatientStatisticsByAgeDTO();
        statistics.setChild(childCount);
        statistics.setAdult(adultCount);
        statistics.setElderly(elderlyCount);

        return statistics;
    }

    public Patient getByLogin(String login) {
        return patientRepository.findByLogin(login).orElseThrow(() -> new NotFoundException("Patient not found"));
    }
}
