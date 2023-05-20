package ru.javavlsu.kb.esap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.mapper.DoctorMapper;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.service.DoctorService;
import ru.javavlsu.kb.esap.util.DoctorUtils;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;
    private final DoctorUtils doctorUtils;

    public DoctorController(DoctorService doctorService, DoctorMapper doctorMapper, DoctorUtils doctorUtils) {
        this.doctorService = doctorService;
        this.doctorMapper = doctorMapper;
        this.doctorUtils = doctorUtils;
    }

    @GetMapping("/{page}")
    public List<DoctorDTO> getAllDoctors(@PathVariable("page") int page) {
        Doctor doctor = doctorUtils.getDoctorDetails().getDoctor();
        return doctorMapper.toDoctorDTOList(doctorService.getByClinic(doctor.getClinic(), page));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getDoctorCount() {
        Doctor doctor = doctorUtils.getDoctorDetails().getDoctor();
        return ResponseEntity.ok(doctorService.getDoctorCountByClinic(doctor.getClinic()));
    }

    @GetMapping("/home")
    public DoctorDTO getDoctor() {
        Doctor doctor = doctorUtils.getDoctorDetails().getDoctor();
        doctor = doctorService.refreshDoctor(doctor);
        return doctorMapper.toDoctorDTO(doctor);
    }
}
