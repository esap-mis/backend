package ru.javavlsu.kb.schedule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.common.web.NotFoundException;
import ru.javavlsu.kb.schedule.dto.DoctorRefDTO;
import ru.javavlsu.kb.schedule.mapper.DoctorRefMapper;
import ru.javavlsu.kb.schedule.model.DoctorRef;
import ru.javavlsu.kb.schedule.repository.DoctorRefRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DoctorRefService {

    private final DoctorRefRepository doctorRefRepository;
    private final DoctorRefMapper doctorRefMapper;

    public DoctorRef getById(Long id) {
        return doctorRefRepository.findById(id).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    public List<DoctorRefDTO> findByFullName(String fullName) {
        return doctorRefMapper.toDoctorRefDTOList(doctorRefRepository.findByFullNameContainingIgnoreCase(fullName));
    }

    public List<DoctorRefDTO> findBySpecialization(String specialization) {
        return doctorRefMapper.toDoctorRefDTOList(doctorRefRepository.findBySpecializationIgnoreCase(specialization));
    }

    public List<DoctorRefDTO> findByClinic(Long clinicId) {
        return doctorRefMapper.toDoctorRefDTOList(doctorRefRepository.findByClinicId(clinicId));
    }
}
