package ru.javavlsu.kb.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import ru.javavlsu.kb.auth.AbstractControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class DoctorControllerTest extends AbstractControllerTest {

    @Test
    public void getAllDoctors_ReturnDoctorsOfCurrentUserClinic() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/doctor")
                        .param("page", "0").param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(2, jsonNode.get("content").size());
    }

    @Test
    public void getDoctorCount_ReturnCountOfClinic() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/doctor/count")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        assertEquals(2, objectMapper.readTree(mvcResult.getResponse().getContentAsString()).asInt());
    }

    @Test
    public void getDoctorById_ReturnDoctor() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/doctor/11")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(11, jsonNode.get("id").asLong());
        assertEquals("Хирург", jsonNode.get("specialization").asText());
    }

    @Test
    public void getDoctorById_UnknownId_ReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/api/doctor/999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void searchDoctors_BySpecialization_ReturnDoctors() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/api/doctor/search")
                        .param("specialization", "хирург")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertEquals(1, jsonNode.size());
        assertTrue(jsonNode.get(0).get("lastName").asText().equals("Петров"));
    }
}
