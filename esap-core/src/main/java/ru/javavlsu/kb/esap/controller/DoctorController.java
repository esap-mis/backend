package ru.javavlsu.kb.esap.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.mapper.DoctorMapper;
import ru.javavlsu.kb.esap.mapper.PatientMapper;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.model.Patient;
import ru.javavlsu.kb.esap.security.UserDetails;
import ru.javavlsu.kb.esap.service.DoctorService;
import ru.javavlsu.kb.esap.service.PatientService;
import ru.javavlsu.kb.esap.util.UserUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final UserUtils userUtils;
    private final PatientMapper patientMapper;
    private final PatientService patientService;

    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, UserUtils userUtils, PatientMapper patientMapper, PatientService patientService) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.userUtils = userUtils;
        this.patientMapper = patientMapper;
        this.patientService = patientService;
    }

    @GetMapping("")
    public ResponseEntity<Page<DoctorDTO>> getAllDoctors(@RequestParam(required = false, defaultValue = "0") int page) {
        UserDetails ud = userUtils.UserDetails();
        if (ud.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_PATIENT"))) {
            Patient patient = (Patient) ud.getUser();
            return ResponseEntity.ok(doctorService.getByClinic(patient.getClinic(), page));
        } else {
            Doctor doctor = (Doctor) ud.getUser();
            return ResponseEntity.ok(doctorService.getByClinic(doctor.getClinic(), page));
        }
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<DoctorDTO>> getDoctorsWithSchedule(@RequestParam LocalDate date) {
        UserDetails userDetails = userUtils.UserDetails();
        return ResponseEntity.ok(doctorService.getDoctorsWithScheduleOnDate(userDetails.getUser().getClinic(), date));
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('CHIEF_DOCTOR') or hasRole('LABORATORY') or hasRole('REGISTRANT') or hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Integer> getDoctorCount() {
        Doctor doctor = (Doctor) userUtils.UserDetails().getUser();
        return ResponseEntity.ok(doctorService.getDoctorCountByClinic(doctor.getClinic()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable("id") Long doctorId) {
        Doctor doctor = doctorService.getById(doctorId);
        return ResponseEntity.ok(doctorMapper.toDoctorDTO(doctor));
    }

    @PostMapping("{id}/update")
    public ResponseEntity<HttpStatus> updateDoctor(@PathVariable("id") Long doctorId, @RequestBody DoctorDTO doctorDTO) {
        doctorService.update(doctorId, doctorDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // TODO: Нужен запрос для получения информации о текущем пользователе
    @GetMapping("/home")
    public ResponseEntity<?> getUserInfo() {
        UserDetails ud = userUtils.UserDetails();
        if (ud.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_PATIENT"))) {
            Patient patient = patientService.getByLogin(ud.getUser().getLogin());
            return ResponseEntity.ok(patientMapper.toPatientDTO(patient));
        } else {
            Doctor doctor = doctorService.getByLogin(ud.getUser().getLogin());
            return ResponseEntity.ok(doctorMapper.toDoctorDTO(doctor));
        }
    }
}
