package ru.javavlsu.kb.esap.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.javavlsu.kb.esap.dto.AppointmentDTO;
import ru.javavlsu.kb.esap.dto.DoctorAppointmentDTO;
import ru.javavlsu.kb.esap.dto.PatientAppointmentDTO;
import ru.javavlsu.kb.esap.dto.ScheduleResponseDTO.AppointmentResponseDTO;
import ru.javavlsu.kb.esap.model.Appointment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AppointmentMapper {

    private final ModelMapper modelMapper;

    public AppointmentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Appointment toAppointment(AppointmentDTO appointmentDTO) {
        return modelMapper.map(appointmentDTO, Appointment.class);
    }

    public AppointmentDTO toAppointmentDTO(Appointment appointment) {
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    public List<AppointmentResponseDTO> toAppointmentResponseDTOList(List<Appointment> appointments) {
        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentResponseDTO.class))
                .collect(Collectors.toList());
    }

    public List<PatientAppointmentDTO> toPatientAppointmentDTOList(List<Appointment> appointments) {
        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, PatientAppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public List<DoctorAppointmentDTO> toDoctorAppointmentDTOList(List<Appointment> appointments) {
        return appointments.stream()
                .map(appointment -> modelMapper.map(appointment, DoctorAppointmentDTO.class))
                .collect(Collectors.toList());
    }
}

