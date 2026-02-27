package ru.javavlsu.kb.esap.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.esap.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.model.Appointment;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TimeSlotMapper.class, DoctorMapper.class})
public interface AppointmentMapper {
    Appointment toAppointment(AppointmentDTO appointmentDTO);
    AppointmentDTO toAppointmentDTO(Appointment appointment);
    @Mapping(target = "date", source = "schedule.date")
    AppointmentResponseDTO toAppointmentResponseDTO(Appointment appointment);
    List<AppointmentResponseDTO> toAppointmentResponseDTOList(List<Appointment> appointments);
    @Mapping(target = "date", source = "schedule.date")
    PatientAppointmentDTO toPatientAppointmentDTO(Appointment appointment);
    List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<Appointment> appointments);
    @Mapping(target = "date", source = "schedule.date")
    DoctorAppointmentDTO toDoctorAppointmentDTO(Appointment appointment);
    List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<Appointment> appointments);
}

