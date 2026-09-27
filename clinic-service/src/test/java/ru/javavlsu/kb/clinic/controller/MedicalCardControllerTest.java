package ru.javavlsu.kb.clinic.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import ru.javavlsu.kb.clinic.AbstractControllerTest;
import ru.javavlsu.kb.clinic.dto.AnalysisRequestDTO;
import ru.javavlsu.kb.clinic.dto.MedicalRecordRequestDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
class MedicalCardControllerTest extends AbstractControllerTest {

    @Test
    public void getMedicalCard_ReturnMedicalCardWithPatient() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/medicalCard/patient/10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertNotNull(jsonNode.get("id"));
        assertEquals(10, jsonNode.get("id").asLong());
        assertEquals(10, jsonNode.get("patient").get("id").asLong());
        assertEquals("Иванов", jsonNode.get("patient").get("lastName").asText());
    }

    @Test
    public void getMedicalCard_UnknownPatient_ReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/api/medicalCard/patient/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveMedicalRecord_ValidRequest_SaveAndNotifyPatient() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO(
                "Осмотр", null, null, List.of(new AnalysisRequestDTO("Общий анализ крови", null)));

        this.mockMvc.perform(post("/api/medicalCard/patient/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult mvcResult = this.mockMvc.perform(get("/api/medicalCard/patient/10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode records = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("medicalRecord");
        assertEquals(1, records.size());
        assertEquals("Осмотр", records.get(0).get("record").asText());
        assertTrue(records.get(0).get("fioAndSpecializationDoctor").asText().startsWith("Терапевт"));
        assertNotNull(records.get(0).get("date"));

        verify(eventPublisher).sendNotificationEvent(any());
    }

    @Test
    public void saveMedicalRecord_InvalidRequest_ReturnBadRequest() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO(null, null, null, null);

        this.mockMvc.perform(post("/api/medicalCard/patient/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void saveMedicalRecord_WithoutToken_ReturnUnauthorized() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO("Осмотр", null, null, List.of());

        this.mockMvc.perform(post("/api/medicalCard/patient/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
