package ru.javavlsu.kb.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import ru.javavlsu.kb.auth.AbstractControllerTest;
import ru.javavlsu.kb.auth.dto.PatientDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class PatientControllerTest extends AbstractControllerTest {

    @Test
    public void getPatientsCount_ReturnCountOfClinic() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/patient/count")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(1, objectMapper.readTree(mvcResult.getResponse().getContentAsString()).asInt());
    }

    @Test
    public void getPatient_ReturnPatient() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/patient/10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(10, jsonNode.get("id").asLong());
        assertEquals("Иванов", jsonNode.get("lastName").asText());
    }

    @Test
    public void getPatientStatisticsByGender_ReturnMaleAndFemale() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/patient/statistics/by-gender")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(1, jsonNode.get("male").asInt());
        assertEquals(0, jsonNode.get("female").asInt());
    }

    @Test
    public void createPatient_PublishesProfileEvent() throws Exception {
        PatientDTO patientDTO = new PatientDTO(null, "Пётр", "Петрович", "Сидоров",
                LocalDate.of(1985, 3, 1), 1, "ул. Мира, 1", "+7(999)000-00-00", "petrov@mail.ru");

        this.mockMvc.perform(post("/api/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void createPatient_NotValidBody_ReturnBadRequest() throws Exception {
        PatientDTO patientDTO = new PatientDTO(null, "", "", "",
                null, 5, "", "", "не-email");

        this.mockMvc.perform(post("/api/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getLatestPatients_ReturnList() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/patient/latest")
                        .param("count", "5")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonNode.size() >= 1);
    }
}
