package com.onevoker.timetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.controllers.security.annotations.WithDefaultUser;
import com.onevoker.timetracker.domain.services.interfaces.UserService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.security.services.VerifyService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc api;

    @MockBean
    private UserService userService;

    @MockBean
    private VerifyService verifyService;

    private ObjectMapper objectMapper;

    private static final String USERS_ENDPOINT = "/users";
    private static final String USER_ID_PARAM = "/{id}";
    private static final String RECORDS_ENDPOINT = "/{id}/records";
    private static final String PROJECTS_ENDPOINT = "/{id}/projects";

    private static final Integer USER_ID = 1;
    private static final User USER = new User("Test User");
    private static final Project PROJECT = new Project("Test Project", "Test Description");
    private static final AuthRequest AUTH_REQUEST = AuthRequest.builder()
            .username("Test User")
            .password("1234")
            .build();
    private static final RecordResponse RECORD_RESPONSE = new RecordResponse("Test Project",
            "Test User",
            5,
            "Test Description",
            OffsetDateTime.now());

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testGetAllUsersUnauthorized() throws Exception {
        api.perform(get(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithDefaultUser
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(USER));

        api.perform(get(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(USER))));
    }

    @Test
    @WithDefaultUser
    void testGetUserById() throws Exception {
        when(userService.getUserById(anyInt())).thenReturn(USER);

        api.perform(get(USERS_ENDPOINT + USER_ID_PARAM, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(USER)));
    }

    @Test
    @WithDefaultUser
    void testGetUserRecords() throws Exception {
        when(userService.getAllRecordsById(anyInt())).thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(USERS_ENDPOINT + RECORDS_ENDPOINT, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }

    @Test
    @WithDefaultUser
    void testGetUserProjects() throws Exception {
        when(userService.getAllProjectsById(anyInt())).thenReturn(List.of(PROJECT));

        api.perform(get(USERS_ENDPOINT + PROJECTS_ENDPOINT, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(PROJECT))));
    }

    @Test
    @WithDefaultUser
    void testUpdateUser() throws Exception {
        doNothing().when(verifyService).verifyUserId(anyInt(), any(UserPrincipal.class));
        doNothing().when(userService).updateUser(anyInt(), any(AuthRequest.class));

        String requestBody = objectMapper.writeValueAsString(AUTH_REQUEST);

        api.perform(put(USERS_ENDPOINT + USER_ID_PARAM, USER_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithDefaultUser
    void testDeleteUser() throws Exception {
        doNothing().when(verifyService).verifyUserIdOrAdmin(anyInt(), any(UserPrincipal.class));
        doNothing().when(userService).deleteUser(anyInt());

        api.perform(delete(USERS_ENDPOINT + USER_ID_PARAM, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
