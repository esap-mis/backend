package ru.javavlsu.kb.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.auth.dto.ClinicDTO;
import ru.javavlsu.kb.auth.mapper.ClinicMapper;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.repository.ClinicRepository;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClinicService {

    private final ClinicRepository clinicRepository;
    private final ClinicMapper clinicMapper;

    public ClinicService(ClinicRepository clinicRepository, ClinicMapper clinicMapper) {
        this.clinicRepository = clinicRepository;
        this.clinicMapper = clinicMapper;
    }

    public List<Clinic> getAll() {
        return clinicRepository.findAll();
    }

    public List<ClinicDTO> getAllDTO() {
        return clinicRepository.findAll().stream().map(clinicMapper::toClinicDTO).toList();
    }

    public Clinic getById(Long id) {
        return clinicRepository.findById(id).orElseThrow(() -> new NotFoundException("Clinic not found"));
    }
}
