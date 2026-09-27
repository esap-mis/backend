package ru.javavlsu.kb.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.auth.dto.DoctorDTO;
import ru.javavlsu.kb.auth.dto.DoctorResponseDTO;
import ru.javavlsu.kb.auth.mapper.DoctorMapper;
import ru.javavlsu.kb.auth.service.DoctorService;
import ru.javavlsu.kb.common.security.CurrentUser;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final CurrentUser currentUser;

    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, CurrentUser currentUser) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.currentUser = currentUser;
    }

    @GetMapping("")
    public ResponseEntity<?> getAllDoctors(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(doctorService.getByClinic(requireClinicId(), page, size));
    }

    @GetMapping("/count")
    @PreAuthorize("hasAnyRole('CHIEF_DOCTOR', 'LABORATORY', 'REGISTRANT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Integer> getDoctorCount() {
        return ResponseEntity.ok(doctorService.getDoctorCountByClinic(requireClinicId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable("id") Long doctorId) {
        return ResponseEntity.ok(doctorMapper.toDoctorDTO(doctorService.getById(doctorId)));
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<HttpStatus> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody DoctorDTO doctorDTO) {
        doctorService.update(doctorId, doctorDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/home")
    public ResponseEntity<DoctorDTO> getUserInfo() {
        return ResponseEntity.ok(doctorMapper.toDoctorDTO(doctorService.getById(currentUser.id())));
    }

    @GetMapping("/search")
    public List<DoctorResponseDTO> searchDoctors(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String specialization) {
        if (fullName != null && !fullName.isBlank()) {
            return doctorService.findByFullName(fullName);
        }
        if (specialization != null && !specialization.isBlank()) {
            return doctorService.findBySpecialization(specialization);
        }
        return doctorService.findByClinic(requireClinicId());
    }

    private Long requireClinicId() {
        Long clinicId = currentUser.clinicId();
        if (clinicId == null) {
            throw new NotFoundException("Clinic not found for current user");
        }
        return clinicId;
    }
}
