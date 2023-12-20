package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.dto.PatientDTO;
import ru.javavlsu.kb.esap.dto.PatientStatisticsByAgeDTO;
import ru.javavlsu.kb.esap.dto.PatientStatisticsByGenderDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.PatientResponseDTO;
import ru.javavlsu.kb.esap.mapper.PatientMapper;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.service.AppointmentService;
import ru.javavlsu.kb.esap.util.KafkaProducer;
import ru.javavlsu.kb.esap.service.PatientService;
import ru.javavlsu.kb.esap.exception.NotCreateException;
import ru.javavlsu.kb.esap.exception.ResponseMessageError;
import ru.javavlsu.kb.esap.util.PatientWithPassword;
import ru.javavlsu.kb.esap.util.UserUtils;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/patient")
public class PatientController {

    private final KafkaProducer kafkaProducer;
    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final UserUtils userUtils;
    private final AppointmentService appointmentService;

    public PatientController(KafkaProducer kafkaProducer, PatientService patientService, PatientMapper patientMapper, UserUtils userUtils, AppointmentService appointmentService) {
        this.kafkaProducer = kafkaProducer;
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.userUtils = userUtils;
        this.appointmentService = appointmentService;
    }

    @GetMapping("")
    public ResponseEntity<Page<PatientResponseDTO>> getAllPatients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String patronymic,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        Page<PatientResponseDTO> patients = patientService.getByClinic(firstName, patronymic, lastName, doctor.getClinic(), page, size);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('CHIEF_DOCTOR') or hasRole('LABORATORY') or hasRole('REGISTRANT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Integer> getPatientsCount() {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        return ResponseEntity.ok(patientService.getPatientCountByClinic(doctor.getClinic()));
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('CHIEF_DOCTOR') or hasRole('LABORATORY') or hasRole('REGISTRANT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<PatientResponseDTO>> getLatestPatients(@RequestParam(defaultValue = "5") int count) {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        return ResponseEntity.ok(patientService.getLatestPatients(count, doctor.getClinic()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable("id") Long patientId) {
        Patient patient = patientService.getById(patientId);
        return ResponseEntity.ok(patientMapper.toPatientResponseDTO(patient));
    }

    @PostMapping
//    @PreAuthorize("hasRole('REGISTRANT')")
    public ResponseEntity<HttpStatus> createPatient(@Valid @RequestBody PatientDTO patientDTO,
                                                    BindingResult bindingResult) throws JsonProcessingException {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        PatientWithPassword patientWithPassword = patientService.create(patientDTO, doctor.getClinic());
        Patient createdPatient = patientWithPassword.getPatient();
        createdPatient.setPassword(patientWithPassword.getDecryptedPassword());
        kafkaProducer.sendMessage(createdPatient);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("{id}/update")
    @PreAuthorize("hasRole('REGISTRANT')")
    public ResponseEntity<HttpStatus> updatePatient(@PathVariable("id") Long patientId, @Valid @RequestBody PatientDTO patientDTO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        patientService.update(patientId, patientDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/statistics/by-gender")
    @PreAuthorize("hasRole('CHIEF_DOCTOR') or hasRole('LABORATORY') or hasRole('REGISTRANT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<PatientStatisticsByGenderDTO> getPatientStatisticsByGender() {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        return ResponseEntity.ok(patientService.getPatientsStatisticsByGender(doctor.getClinic()));
    }

    @GetMapping("/statistics/by-age")
    @PreAuthorize("hasRole('CHIEF_DOCTOR') or hasRole('LABORATORY') or hasRole('REGISTRANT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<PatientStatisticsByAgeDTO> getPatientStatisticsByAge() {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        return ResponseEntity.ok(patientService.getPatientsStatisticsByAge(doctor.getClinic()));
    }

}
