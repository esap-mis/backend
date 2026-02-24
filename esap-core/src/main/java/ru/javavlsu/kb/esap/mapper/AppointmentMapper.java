package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.esap.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.model.Appointment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    Appointment toAppointment(AppointmentDTO appointmentDTO);
    AppointmentDTO toAppointmentDTO(Appointment appointment);
    List<AppointmentResponseDTO> toAppointmentResponseDTOList(List<Appointment> appointments);
    List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<Appointment> appointments);
    List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<Appointment> appointments);
}

