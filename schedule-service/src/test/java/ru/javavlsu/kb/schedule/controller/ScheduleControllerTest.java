package ru.javavlsu.kb.schedule.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import ru.javavlsu.kb.schedule.AbstractControllerTest;
import ru.javavlsu.kb.schedule.dto.AppointmentDTO;
import ru.javavlsu.kb.schedule.dto.ScheduleDTO;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class ScheduleControllerTest extends AbstractControllerTest {

    private static final LocalDate DATE = LocalDate.now().plusDays(1);

    @Test
    public void createSchedule_ValidRequest_ReturnScheduleId() throws Exception {
        ScheduleDTO request = new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0));

        MvcResult mvcResult = this.mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + doctorToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonNode.get("scheduleId").asLong() > 0);
    }

    @Test
    public void createSchedule_DuplicatedDateAndDoctor_ReturnBadRequest() throws Exception {
        ScheduleDTO request = new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0));
        createSchedule(request);

        this.mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createSchedule_InvalidTimeRange_ReturnBadRequest() throws Exception {
        ScheduleDTO request = new ScheduleDTO(10L, DATE, LocalTime.of(11, 0), LocalTime.of(9, 0));

        this.mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + doctorToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAvailableAppointments_BeforeBooking_ReturnSlots() throws Exception {
        long scheduleId = createSchedule(new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0)));

        MvcResult mvcResult = this.mockMvc.perform(get("/api/schedule/available")
                        .param("doctorId", "10").param("date", DATE.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(4, jsonNode.size());
        assertEquals("09:00", jsonNode.get(0).asText().substring(0, 5));
        assertTrue(scheduleId > 0);
    }

    @Test
    public void addAppointment_ValidRequest_PublishesEventAndTakesSlot() throws Exception {
        long scheduleId = createSchedule(new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        AppointmentDTO request = new AppointmentDTO(12L, DATE, LocalTime.of(9, 0));

        MvcResult mvcResult = this.mockMvc.perform(post("/api/schedule/" + scheduleId + "/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(12, jsonNode.get("patient").get("id").asLong());
        assertEquals("CONFIRMED", jsonNode.get("status").asText());

        verify(eventPublisher).sendAppointmentCreatedEvent(any());

        MvcResult available = this.mockMvc.perform(get("/api/schedule/available")
                        .param("doctorId", "10").param("date", DATE.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andExpect(status().is2xxSuccessful()).andReturn();
        assertEquals(3, objectMapper.readTree(available.getResponse().getContentAsString()).size());
    }

    @Test
    public void addAppointment_TimeAlreadyTaken_ReturnBadRequest() throws Exception {
        long scheduleId = createSchedule(new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        AppointmentDTO request = new AppointmentDTO(12L, DATE, LocalTime.of(9, 0));

        this.mockMvc.perform(post("/api/schedule/" + scheduleId + "/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andExpect(status().is2xxSuccessful());

        AppointmentDTO second = new AppointmentDTO(10L, DATE, LocalTime.of(9, 0));
        this.mockMvc.perform(post("/api/schedule/" + scheduleId + "/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addAppointment_UnknownPatient_ReturnNotFound() throws Exception {
        long scheduleId = createSchedule(new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        AppointmentDTO request = new AppointmentDTO(999L, DATE, LocalTime.of(9, 0));

        this.mockMvc.perform(post("/api/schedule/" + scheduleId + "/appointment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getDoctors_BySpecialization_ReturnDoctors() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/schedule/doctors")
                        .param("specialization", "хирург")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + patientToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(1, jsonNode.size());
    }

    @Test
    public void createSchedule_WithoutToken_ReturnUnauthorized() throws Exception {
        ScheduleDTO request = new ScheduleDTO(10L, DATE, LocalTime.of(9, 0), LocalTime.of(11, 0));

        this.mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    private long createSchedule(ScheduleDTO request) throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + doctorToken))
                .andExpect(status().is2xxSuccessful()).andReturn();
        return objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("scheduleId").asLong();
    }
}
