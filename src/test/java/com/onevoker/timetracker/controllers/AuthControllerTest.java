package com.onevoker.timetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.dto.auth.AuthResponse;
import com.onevoker.timetracker.security.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc api;

    @MockBean
    private AuthService authService;

    private ObjectMapper objectMapper;

    private static final String AUTH_ENDPOINT = "/auth";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String LOGIN_ENDPOINT = "/login";

    private static final AuthRequest AUTH_REQUEST = AuthRequest.builder()
            .username("Test User")
            .password("pdapw")
            .build();
    private static final AuthResponse AUTH_RESPONSE = new AuthResponse("testToken123");

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegister() throws Exception {
        when(authService.register(any(AuthRequest.class))).thenReturn(AUTH_RESPONSE);

        String requestBody = objectMapper.writeValueAsString(AUTH_REQUEST);
        String responseBody = objectMapper.writeValueAsString(AUTH_RESPONSE);

        api.perform(post(AUTH_ENDPOINT + REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    void testLogin() throws Exception {
        when(authService.login(any(AuthRequest.class))).thenReturn(AUTH_RESPONSE);

        String requestBody = objectMapper.writeValueAsString(AUTH_REQUEST);
        String responseBody = objectMapper.writeValueAsString(AUTH_RESPONSE);

        api.perform(post(AUTH_ENDPOINT + LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @Test
    void testRegisterValidationFailure() throws Exception {
        AuthRequest invalidRequest = AuthRequest.builder()
                .username("")
                .password("")
                .build();

        String requestBody = objectMapper.writeValueAsString(invalidRequest);

        api.perform(post(AUTH_ENDPOINT + REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginValidationFailure() throws Exception {
        AuthRequest invalidRequest = AuthRequest.builder()
                .username("")
                .password("")
                .build();

        String requestBody = objectMapper.writeValueAsString(invalidRequest);

        api.perform(post(AUTH_ENDPOINT + LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
