package ru.javavlsu.kb.auth.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.auth.dto.PatientDTO;
import ru.javavlsu.kb.auth.dto.PatientResponseDTO;
import ru.javavlsu.kb.auth.dto.PatientStatisticsByAgeDTO;
import ru.javavlsu.kb.auth.dto.PatientStatisticsByGenderDTO;
import ru.javavlsu.kb.auth.mapper.PatientMapper;
import ru.javavlsu.kb.auth.service.PatientService;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.common.web.NotCreateException;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.common.web.ResponseMessageError;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;
    private final CurrentUser currentUser;

    public PatientController(PatientService patientService, PatientMapper patientMapper, CurrentUser currentUser) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.currentUser = currentUser;
    }

    @GetMapping("")
    public ResponseEntity<Page<PatientResponseDTO>> getAllPatients(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String patronymic,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(patientService.getByClinic(
                firstName, patronymic, lastName, requireClinicId(), page, size));
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Integer> getPatientsCount() {
        return ResponseEntity.ok(patientService.getPatientCountByClinic(requireClinicId()));
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<PatientResponseDTO>> getLatestPatients(@RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(patientService.getLatestPatients(count, requireClinicId()));
    }

    @GetMapping("/statistics/by-gender")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<PatientStatisticsByGenderDTO> getPatientStatisticsByGender() {
        return ResponseEntity.ok(patientService.getPatientsStatisticsByGender(requireClinicId()));
    }

    @GetMapping("/statistics/by-age")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<PatientStatisticsByAgeDTO> getPatientStatisticsByAge() {
        return ResponseEntity.ok(patientService.getPatientsStatisticsByAge(requireClinicId()));
    }

    @GetMapping("/search")
    public List<PatientResponseDTO> searchPatients(@RequestParam String fullName) {
        return patientService.findByFullName(fullName);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable("id") Long patientId) {
        return ResponseEntity.ok(patientMapper.toPatientResponseDTO(patientService.getById(patientId)));
    }

    @GetMapping("/home")
    public ResponseEntity<PatientDTO> getUserInfo() {
        return ResponseEntity.ok(patientMapper.toPatientDTO(patientService.getById(currentUser.id())));
    }

    @PostMapping
    public ResponseEntity<HttpStatus> createPatient(@Valid @RequestBody PatientDTO patientDTO,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        patientService.create(patientDTO, requireClinicId());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('REGISTRANT')")
    public ResponseEntity<HttpStatus> updatePatient(@PathVariable("id") Long patientId,
                                                    @Valid @RequestBody PatientDTO patientDTO,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new NotCreateException(ResponseMessageError.createErrorMsg(bindingResult.getFieldErrors()));
        }
        patientService.update(patientId, patientDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Long requireClinicId() {
        Long clinicId = currentUser.clinicId();
        if (clinicId == null) {
            throw new NotFoundException("Clinic not found for current user");
        }
        return clinicId;
    }
}
