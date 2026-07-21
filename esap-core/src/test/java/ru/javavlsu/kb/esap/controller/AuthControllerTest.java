package ru.javavlsu.kb.esap.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.javavlsu.kb.esap.dto.auth.AuthenticationDTO;
import ru.javavlsu.kb.esap.dto.auth.ClinicRegistration;
import ru.javavlsu.kb.esap.dto.auth.ClinicRegistrationDTO;
import ru.javavlsu.kb.esap.dto.auth.DoctorRegistration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class AuthControllerTest extends AbstractControllerTest {

    @Test
    public void registrationClinic_SuccessfulRegistrationClinic_ReturnLoginPassword() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration(null, null, null,
                "Test1", "Test3", "Test2", "specialization", 1, "DOCTOR");
        ClinicRegistration clinicRegistration = new ClinicRegistration("TestClinic", "TestAddress", "88005553535");
        ClinicRegistrationDTO clinicRegistrationDTO = new ClinicRegistrationDTO(clinicRegistration, doctorRegistration);

        String requestBody = objectMapper.writeValueAsString(clinicRegistrationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        assertNotNull(jsonNode.get("login").asText());
        assertNotNull(jsonNode.get("password").asText());
        assertFalse(jsonNode.get("login").asText().isBlank());
        assertFalse(jsonNode.get("password").asText().isBlank());
    }

    @Test
    public void registrationClinic_NotValidRegistrationClinic_ReturnNotValidField() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration(null, null, null,
                "", "", "", "", 0, "    ");
        ClinicRegistration clinicRegistration = new ClinicRegistration("", "", "112");
        ClinicRegistrationDTO clinicRegistrationDTO = new ClinicRegistrationDTO(clinicRegistration, doctorRegistration);

        String requestBody = objectMapper.writeValueAsString(clinicRegistrationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void performLogin_SuccessfulLogin_ReturnJwtTokenAndRole() throws Exception {
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("admin", "123");

        String requestBody = objectMapper.writeValueAsString(authenticationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        assertNotNull(jsonNode.get("jwt").asText());
        assertNotNull(jsonNode.get("jwt").asText());
        assertFalse(jsonNode.get("roles").asText().isBlank());
        assertFalse(jsonNode.get("roles").asText().isBlank());
        assertEquals(jwtUtil.validateToken(jsonNode.get("jwt").asText()), authenticationDTO.login());
    }

    @Test
    public void registrationDoctor_SuccessfulRegistrationDoctor_ReturnLoginPassword() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration(null, null, null,
                "Test1", "Test3", "Test2", "specialization", 1, "DOCTOR");

        String requestBody = objectMapper.writeValueAsString(doctorRegistration);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/doctor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + jwtUtil.generateToken("admin"));

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        assertNotNull(jsonNode.get("login").asText());
        assertNotNull(jsonNode.get("password").asText());
        assertFalse(jsonNode.get("login").asText().isBlank());
        assertFalse(jsonNode.get("password").asText().isBlank());
    }

    @Test
    public void registrationDoctor_NotValidRegistrationDoctor_ReturnNotValidField() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration(null, null, null,
                "", "", "", "", 0, "    ");

        String requestBody = objectMapper.writeValueAsString(doctorRegistration);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/doctor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header("Authorization", "Bearer " + jwtUtil.generateToken("admin"));

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is4xxClientError()).andReturn();
    }


}