package ru.javavlsu.kb.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.javavlsu.kb.auth.AbstractControllerTest;
import ru.javavlsu.kb.auth.dto.ClinicRegistration;
import ru.javavlsu.kb.auth.dto.ClinicRegistrationDTO;
import ru.javavlsu.kb.auth.dto.DoctorRegistration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(value = "classpath:init-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "classpath:clean-user.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class AuthControllerTest extends AbstractControllerTest {

    @Test
    public void registrationClinic_SuccessfulRegistrationClinic_ReturnLoginPassword() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration("Test1", "Test3", "Test2", "specialization", 1, "DOCTOR");
        ClinicRegistration clinicRegistration = new ClinicRegistration("TestClinic", "TestAddress", "88005553535");
        ClinicRegistrationDTO clinicRegistrationDTO = new ClinicRegistrationDTO(clinicRegistration, doctorRegistration);

        String requestBody = objectMapper.writeValueAsString(clinicRegistrationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/clinic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(jsonNode.get("login").asText().isBlank());
        assertFalse(jsonNode.get("password").asText().isBlank());
    }

    @Test
    public void registrationClinic_NotValidRegistrationClinic_ReturnNotValidField() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration("", "", "", "", 0, "    ");
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
    public void performLogin_SuccessfulLogin_ReturnJwtTokenAndRoles() throws Exception {
        var authenticationDTO = new ru.javavlsu.kb.auth.dto.AuthenticationDTO("admin", "123");

        String requestBody = objectMapper.writeValueAsString(authenticationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(jsonNode.get("jwt").asText().isBlank());
        assertFalse(jsonNode.get("roles").asText().isBlank());
        assertEquals("admin", jwtTokenProvider.validateToken(jsonNode.get("jwt").asText()).login());
        assertEquals(10L, jwtTokenProvider.validateToken(jsonNode.get("jwt").asText()).clinicId());
    }

    @Test
    public void performLogin_PatientFromDesktop_ReturnBadRequest() throws Exception {
        var authenticationDTO = new ru.javavlsu.kb.auth.dto.AuthenticationDTO("patient1", "123");

        String requestBody = objectMapper.writeValueAsString(authenticationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void performLogin_PatientFromMobile_ReturnJwtToken() throws Exception {
        var authenticationDTO = new ru.javavlsu.kb.auth.dto.AuthenticationDTO("patient1", "123");

        String requestBody = objectMapper.writeValueAsString(authenticationDTO);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(HttpHeaders.USER_AGENT, "mobile-app");

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();
        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(jsonNode.get("jwt").asText().isBlank());
    }

    @Test
    public void registrationDoctor_SuccessfulRegistrationDoctor_ReturnLoginPassword() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration("Test1", "Test3", "Test2", "specialization", 1, "DOCTOR");

        String requestBody = objectMapper.writeValueAsString(doctorRegistration);
        MockHttpServletRequestBuilder requestBuilder = post("/api/auth/registration/doctor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);

        MvcResult mvcResult = this.mockMvc.perform(requestBuilder)
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertFalse(jsonNode.get("login").asText().isBlank());
        assertFalse(jsonNode.get("password").asText().isBlank());
    }

    @Test
    public void registrationDoctor_WithoutToken_ReturnUnauthorized() throws Exception {
        DoctorRegistration doctorRegistration = new DoctorRegistration("Test1", "Test3", "Test2", "specialization", 1, "DOCTOR");

        String requestBody = objectMapper.writeValueAsString(doctorRegistration);
        this.mockMvc.perform(post("/api/auth/registration/doctor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllRoles_ReturnRolesWithoutPrefix() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/roles")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken))
                .andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

        JsonNode jsonNode = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        assertTrue(jsonNode.toString().contains("CHIEF_DOCTOR"));
    }
}
