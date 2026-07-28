package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordRequestDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class MedicalCardControllerTest extends AbstractControllerTest {

    @Test
    public void getMedicalCard_ReturnMedicalCard() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = get("/api/medicalCard/patient/10")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + bearerToken);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        assertNotNull(jsonNode.get("id").asText());
        assertNotNull(jsonNode.get("patient"));
        assertFalse(jsonNode.get("id").asText().isBlank());
    }

    @Test
    public void saveMedicalRecord_NotValidMedicalRecordFioDoctor_ReturnMedicalCard() throws Exception {
        MedicalRecordRequestDTO medicalRecordRequestDTO = new MedicalRecordRequestDTO(null, null, null, null);
        String requestBody = objectMapper.writeValueAsString(medicalRecordRequestDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/medicalCard/patient/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is4xxClientError());
    }

}