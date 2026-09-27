package ru.javavlsu.kb.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.javavlsu.kb.auth.dto.ClinicDTO;
import ru.javavlsu.kb.auth.service.ClinicService;

import java.util.List;

@RestController
@RequestMapping("/api/clinic")
public class ClinicController {

    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @GetMapping
    public List<ClinicDTO> getAllClinics() {
        return clinicService.getAllDTO();
    }
}
