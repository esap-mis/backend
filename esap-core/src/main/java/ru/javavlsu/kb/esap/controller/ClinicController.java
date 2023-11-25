package ru.javavlsu.kb.esap.controller;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.service.ClinicService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/clinic")
public class ClinicController {

    private final ClinicService clinicService;
    private final ModelMapper modelMapper;

    public ClinicController(ClinicService clinicService, ModelMapper modelMapper) {
        this.clinicService = clinicService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<Clinic> getAllClinics() {
        return clinicService.getAll();
    }
}
