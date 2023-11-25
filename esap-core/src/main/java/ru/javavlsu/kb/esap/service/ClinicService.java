package ru.javavlsu.kb.esap.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.repository.ClinicRepository;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClinicService {

    
    private ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository, DoctorService doctorService) {
        this.clinicRepository = clinicRepository;
    }

    public List<Clinic> getAll() { 
        return clinicRepository.findAll(); 
    }

}
