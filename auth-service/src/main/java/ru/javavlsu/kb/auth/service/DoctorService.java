package ru.javavlsu.kb.auth.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javavlsu.kb.auth.dto.DoctorDTO;
import ru.javavlsu.kb.auth.dto.DoctorResponseDTO;
import ru.javavlsu.kb.auth.mapper.DoctorMapper;
import ru.javavlsu.kb.auth.model.Clinic;
import ru.javavlsu.kb.auth.model.Doctor;
import ru.javavlsu.kb.auth.repository.ClinicRepository;
import ru.javavlsu.kb.auth.repository.DoctorRepository;
import ru.javavlsu.kb.common.kafka.EventPublisher;
import ru.javavlsu.kb.common.web.NotFoundException;

import java.util.List;

/**
 * Владеет справочником врачей. Расписания и записи — в schedule-service,
 * здесь они намеренно не хранятся.
 */
@Service
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;
    private final ClinicRepository clinicRepository;
    private final EventPublisher eventPublisher;

    public DoctorService(DoctorMapper doctorMapper, DoctorRepository doctorRepository,
                         ClinicRepository clinicRepository, EventPublisher eventPublisher) {
        this.doctorMapper = doctorMapper;
        this.doctorRepository = doctorRepository;
        this.clinicRepository = clinicRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public int getDoctorCountByClinic(Long clinicId) {
        return doctorRepository.countDoctorByClinic(requireClinic(clinicId));
    }

    public Page<DoctorDTO> getByClinic(Long clinicId, int page, int size) {
        return doctorMapper.toDoctorDTOPage(
                doctorRepository.findByClinicOrderByIdAsc(requireClinic(clinicId), org.springframework.data.domain.PageRequest.of(page, size)));
    }

    public Doctor getById(long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    public Doctor getByLogin(String login) {
        return doctorRepository.findByLogin(login).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    @Transactional
    public Doctor update(Long doctorId, DoctorDTO doctorDTO) {
        Doctor doctor = getById(doctorId);
        doctor.setFirstName(doctorDTO.firstName());
        doctor.setPatronymic(doctorDTO.patronymic());
        doctor.setLastName(doctorDTO.lastName());
        doctor.setGender(doctorDTO.gender());
        doctor.setSpecialization(doctorDTO.specialization());
        Doctor saved = doctorRepository.save(doctor);
        eventPublisher.sendUserProfileEvent(UserProfileFactory.from(saved, saved.getClinic()));
        return saved;
    }

    public List<DoctorResponseDTO> findByFullName(String fullName) {
        List<Doctor> doctors = doctorRepository.findByFullName(fullName);
        if (doctors.isEmpty()) {
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.toDoctorResponseDTOList(doctors);
    }

    public List<DoctorResponseDTO> findBySpecialization(String specialization) {
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        if (doctors.isEmpty()) {
            throw new NotFoundException("Doctor not found");
        }
        return doctorMapper.toDoctorResponseDTOList(doctors);
    }

    public List<DoctorResponseDTO> findByClinic(Long clinicId) {
        return doctorMapper.toDoctorResponseDTOList(doctorRepository.findByClinicOrderByIdAsc(
                requireClinic(clinicId), org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent());
    }

    private Clinic requireClinic(Long clinicId) {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Clinic not found"));
    }
}
