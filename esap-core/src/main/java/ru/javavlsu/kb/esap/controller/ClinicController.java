package ru.javavlsu.kb.esap.controller;

import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.service.ClinicService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/clinic")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public List<Clinic> getAllClinics() {
        return clinicService.getAll();
    }
}
