package ru.javavlsu.kb.esap.service;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.javavlsu.kb.esap.dto.DoctorDTO;
import ru.javavlsu.kb.esap.mapper.DoctorMapper;
import ru.javavlsu.kb.esap.model.Clinic;
import ru.javavlsu.kb.esap.model.Doctor;
import ru.javavlsu.kb.esap.repository.DoctorRepository;
import ru.javavlsu.kb.esap.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorService {

    private final EntityManager em;
    private final DoctorMapper doctorMapper;
    private final DoctorRepository doctorRepository;
    private final Logger log = LoggerFactory.getLogger(DoctorService.class);

    public DoctorService(EntityManager em, DoctorMapper doctorMapper, DoctorRepository doctorRepository) {
        this.em = em;
        this.doctorMapper = doctorMapper;
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    public int getDoctorCountByClinic(Clinic clinic) {
        log.debug("class:DoctorService, method:getDoctorCountByClinic, sql:countDoctorByClinic");
        return doctorRepository.countDoctorByClinic(clinic);
    }

    public Page<DoctorDTO> getByClinic(Clinic clinic, int page) {
        log.debug("class:DoctorService, method:getByClinic, sql:findByClinicOrderByIdAsc");
        Page<Doctor> doctors = doctorRepository.findByClinicOrderByIdAsc(clinic, PageRequest.of(page, 10));
        return doctorMapper.toDoctorDTOPage(doctors);
    }

    public List<DoctorDTO> getDoctorsWithScheduleOnDate(Clinic clinic, LocalDate date) {
        log.debug("class:DoctorService, method:getDoctorsWithScheduleOnDate, sql:findByClinicOrderByIdAsc");
        List<Doctor> doctors = doctorRepository.findByClinicAndSchedulesDateOrderByIdAsc(clinic, date);
        return doctorMapper.toDoctorDTOList(doctors);
    }

    public Doctor getById(long id) {
        log.debug("class:DoctorService, method:getById, sql:findById");
        return doctorRepository.findById(id).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    public Doctor getByLogin(String login) {
        log.debug("class:DoctorService, method:getById, sql:findById");
        return doctorRepository.findByLogin(login).orElseThrow(() -> new NotFoundException("Doctor not found"));
    }

    public Doctor refreshDoctor(Doctor doctor) {
        log.debug("class:DoctorService, method:refreshDoctor, sql:merge");
        return em.merge(doctor);
    }

//    public List<String> getRoles(String login){
//        log.debug("class:DoctorService, method:getRoles, sql:findByLogin");
//        return doctorRepository.findByLogin(login)
//                .orElseThrow(() -> new NotFoundException("Doctor not found"))
//                .getRole().stream().map(Role::getName).toList();
//    }
//
//    public boolean doctorExists(String login){
//        log.debug("class:DoctorService, method:doctorExists, sql:findByLogin");
//        return doctorRepository.findByLogin(login).isPresent();
//    }

    @Transactional
    public void save(Doctor doctor) {
        log.debug("class:DoctorService, method:save, sql:save");
        doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor update(Long doctorId, DoctorDTO doctorDTO) {
        log.debug("class:DoctorService, method:update, sql:findById");
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor with id=" + doctorId + " not found"));
        doctor.setFirstName(doctorDTO.getFirstName());
        doctor.setPatronymic(doctorDTO.getPatronymic());
        doctor.setLastName(doctorDTO.getLastName());
        doctor.setGender(doctorDTO.getGender());
        doctor.setSpecialization(doctorDTO.getSpecialization());
        log.debug("class:DoctorService, method:update, sql:save");
        return doctorRepository.save(doctor);
    }
}
