package ru.javavlsu.kb.core.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.javavlsu.kb.core.dto.RemoteDoctorDTO;
import ru.javavlsu.kb.core.dto.RemotePatientDTO;
import ru.javavlsu.kb.core.dto.RemoteMedicalCardDTO;
import ru.javavlsu.kb.core.dto.RemoteScheduleDTO;
import ru.javavlsu.kb.core.dto.AppointmentRequestDTO;
import ru.javavlsu.kb.core.dto.RemoteAppointmentDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Фасад ко всем остальным доменам. Раньше AI-агент ходил прямо в JPA-репозитории
 * esap-core, теперь он работает только через эти вызовы.
 */
@Component
@RequiredArgsConstructor
public class EsapClient {

    @Qualifier("authClient")
    private final RestClient authClient;
    @Qualifier("clinicClient")
    private final RestClient clinicClient;
    @Qualifier("scheduleClient")
    private final RestClient scheduleClient;

    public RemotePatientDTO getPatient(Long id) {
        return authClient.get().uri("/api/patient/{id}", id)
                .retrieve().body(RemotePatientDTO.class);
    }

    public List<RemotePatientDTO> findPatientsByFullName(String fullName) {
        return List.of(authClient.get().uri("/api/patient/search?fullName={fullName}", fullName)
                .retrieve().body(RemotePatientDTO[].class));
    }

    public List<RemoteDoctorDTO> findDoctorsByFullName(String fullName) {
        return List.of(authClient.get().uri("/api/doctor/search?fullName={fullName}", fullName)
                .retrieve().body(RemoteDoctorDTO[].class));
    }

    public List<RemoteDoctorDTO> findDoctorsBySpecialization(String specialization) {
        return List.of(authClient.get().uri("/api/doctor/search?specialization={spec}", specialization)
                .retrieve().body(RemoteDoctorDTO[].class));
    }

    public RemoteMedicalCardDTO getMedicalCard(Long patientId) {
        return clinicClient.get().uri("/api/medicalCard/patient/{id}", patientId)
                .retrieve().body(RemoteMedicalCardDTO.class);
    }

    public List<RemoteScheduleDTO> getDoctorSchedules(Long doctorId) {
        return List.of(scheduleClient.get().uri("/api/schedule/doctor/{doctorId}", doctorId)
                .retrieve().body(RemoteScheduleDTO[].class));
    }

    public List<LocalTime> getAvailableAppointments(Long doctorId, LocalDate date) {
        return List.of(scheduleClient.get()
                .uri("/api/schedule/available?doctorId={doctorId}&date={date}", doctorId, date)
                .retrieve().body(LocalTime[].class));
    }

    public RemoteAppointmentDTO createAppointment(Long scheduleId, AppointmentRequestDTO request) {
        return scheduleClient.post().uri("/api/schedule/{id}/appointment", scheduleId)
                .body(request)
                .retrieve().body(RemoteAppointmentDTO.class);
    }

    public void cancelAppointment(Long appointmentId) {
        scheduleClient.delete().uri("/api/schedule/appointment/{id}", appointmentId).retrieve().toBodilessEntity();
    }

    public List<?> getUpcomingAppointments() {
        return List.of((Object[]) scheduleClient.get().uri("/api/schedule/appointments/upcoming")
                .retrieve().body(Object[].class));
    }

    public List<?> getPastAppointments() {
        return List.of((Object[]) scheduleClient.get().uri("/api/schedule/appointments/past")
                .retrieve().body(Object[].class));
    }
}
