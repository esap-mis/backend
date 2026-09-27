package ru.javavlsu.kb.schedule.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.schedule.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.schedule.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.schedule.model.Appointment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentViewMapper {

    List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<Appointment> appointments);

    List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<Appointment> appointments);
}
