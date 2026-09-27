package ru.javavlsu.kb.schedule.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.schedule.dto.AppointmentResponseDTO;
import ru.javavlsu.kb.schedule.dto.DoctorRefDTO;
import ru.javavlsu.kb.schedule.dto.PatientRefDTO;
import ru.javavlsu.kb.schedule.model.Appointment;
import ru.javavlsu.kb.schedule.model.DoctorRef;
import ru.javavlsu.kb.schedule.model.PatientRef;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment);

    List<AppointmentResponseDTO> toAppointmentResponseDTOList(List<Appointment> appointments);

    PatientRefDTO toPatientRefDTO(PatientRef patient);

    DoctorRefDTO toDoctorRefDTO(DoctorRef doctor);
}
