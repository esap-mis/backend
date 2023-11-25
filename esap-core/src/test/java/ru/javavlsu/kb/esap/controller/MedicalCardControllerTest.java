package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.javavlsu.kb.esap.dto.MedicalCardDTO.MedicalRecordRequestDTO;
import ru.javavlsu.kb.esap.security.JWTUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MedicalCardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static String bearerToken;


    @BeforeAll
    public static void generateBearerToken(@Autowired JWTUtil jwtUtil){
        bearerToken = jwtUtil.generateToken("admin");
    }


    @Test
    public void getMedicalCard_ReturnMedicalCard() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = get("/api/medicalCard/patient/1")
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
        MockHttpServletRequestBuilder requestBuilder = post("/api/medicalCard/patient/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is4xxClientError());
    }

}