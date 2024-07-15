package com.onevoker.timetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.controllers.security.annotations.WithAdminUser;
import com.onevoker.timetracker.controllers.security.annotations.WithDefaultUser;
import com.onevoker.timetracker.domain.services.interfaces.RecordService;
import com.onevoker.timetracker.dto.ApiErrorResponse;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.security.services.VerifyService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import org.junit.jupiter.api.BeforeAll;
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
class RecordControllerTest extends IntegrationTest {

    @Autowired
    private MockMvc api;

    @MockBean
    private RecordService recordService;

    @MockBean
    private VerifyService verifyService;

    private static ObjectMapper objectMapper;

    private static final String RECORDS_ENDPOINT = "/records";
    private static final String RECORD_ID_PARAM = "/{id}";
    private static final String PROJECTS_USERS_ENDPOINT = "/projects/{projectId}/users/{userId}";
    private static final String PROJECTS_WITH_ID_ENDPOINT = "/projects/{projectId}";
    private static final String USERS_WITH_ID_ENDPOINT = "/users/{userId}";
    private static final String BETWEEN_DATES_ENDPOINT = "/between-dates";
    private static final String START_DATE_PARAM = "startDate";
    private static final String END_DATE_PARAM = "endDate";

    private static final Integer RECORD_ID = 1;
    private static final Integer USER_ID = 1;
    private static final Integer PROJECT_ID = 1;
    private static final RecordRequest RECORD_REQUEST = new RecordRequest(5, "Test Description");
    private static final String START_DATE = "2023-01-01T00:00:00Z";
    private static final String END_DATE = "2023-12-31T23:59:59Z";
    private static final RecordResponse RECORD_RESPONSE = new RecordResponse("Test Project",
            "Test User",
            5,
            "Test Description",
            OffsetDateTime.now()
    );
    private static final ApiErrorResponse UNAUTHORIZED_ERROR = ApiErrorResponse.builder()
            .exceptionName("InsufficientAuthenticationException")
            .exceptionMessage("Full authentication is required to access this resource")
            .code("401 UNAUTHORIZED")
            .build();

    @BeforeAll
    static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void testCreateRecordUnauthorized() throws Exception {
        String requestBody = objectMapper.writeValueAsString(RECORD_REQUEST);

        api.perform(post(RECORDS_ENDPOINT + PROJECTS_USERS_ENDPOINT, PROJECT_ID, USER_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_ERROR)));
    }

    @Test
    @WithDefaultUser
    void testCreateRecord() throws Exception {
        doNothing().when(verifyService).verifyUserId(anyInt(), any(UserPrincipal.class));
        doNothing().when(recordService).createRecord(any(RecordRequest.class), anyInt(), anyInt());

        String requestBody = objectMapper.writeValueAsString(RECORD_REQUEST);

        api.perform(post(RECORDS_ENDPOINT + PROJECTS_USERS_ENDPOINT, PROJECT_ID, USER_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithAdminUser
    void testGetAllRecords() throws Exception {
        when(recordService.getAllRecords()).thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(RECORDS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }

    @Test
    @WithDefaultUser
    void testGetRecordById() throws Exception {
        when(recordService.getRecordById(anyInt())).thenReturn(RECORD_RESPONSE);

        api.perform(get(RECORDS_ENDPOINT + RECORD_ID_PARAM, RECORD_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(RECORD_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testUpdateRecord() throws Exception {
        doNothing().when(verifyService).verifyUserByRecordId(anyInt(), any(UserPrincipal.class));
        doNothing().when(recordService).updateRecord(anyInt(), any(RecordRequest.class));

        String requestBody = objectMapper.writeValueAsString(RECORD_REQUEST);

        api.perform(put(RECORDS_ENDPOINT + RECORD_ID_PARAM, RECORD_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithDefaultUser
    void testDeleteRecord() throws Exception {
        doNothing().when(verifyService).verifyUserForDeleteRecord(anyInt(), any(UserPrincipal.class));
        doNothing().when(recordService).deleteRecord(anyInt());

        api.perform(delete(RECORDS_ENDPOINT + RECORD_ID_PARAM, RECORD_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithDefaultUser
    void testGetRecordsOnProjectBetweenDates() throws Exception {
        when(recordService.getRecordsOnProjectBetweenDates(anyInt(), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(RECORDS_ENDPOINT + PROJECTS_WITH_ID_ENDPOINT + BETWEEN_DATES_ENDPOINT, PROJECT_ID)
                        .param(START_DATE_PARAM, START_DATE)
                        .param(END_DATE_PARAM, END_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }

    @Test
    @WithDefaultUser
    void testGetRecordsOnProjectForUserBetweenDates() throws Exception {
        when(recordService.getRecordsOnProjectForUserBetweenDates(anyInt(),
                anyInt(),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class)))
                .thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(RECORDS_ENDPOINT + PROJECTS_USERS_ENDPOINT + BETWEEN_DATES_ENDPOINT,
                        PROJECT_ID, USER_ID)
                        .param(START_DATE_PARAM, START_DATE)
                        .param(END_DATE_PARAM, END_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }

    @Test
    @WithDefaultUser
    void testGetUserRecordsAcrossProjectsBetweenDates() throws Exception {
        when(recordService.getUserRecordsAcrossProjectsBetweenDates(anyInt(),
                any(OffsetDateTime.class),
                any(OffsetDateTime.class)))
                .thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(RECORDS_ENDPOINT + USERS_WITH_ID_ENDPOINT + BETWEEN_DATES_ENDPOINT, USER_ID)
                        .param(START_DATE_PARAM, START_DATE)
                        .param(END_DATE_PARAM, END_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }

    @Test
    @WithAdminUser
    void testGetAllRecordsBetweenDates() throws Exception {
        when(recordService.getAllRecordsBetweenDates(any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(List.of(RECORD_RESPONSE));

        api.perform(get(RECORDS_ENDPOINT + BETWEEN_DATES_ENDPOINT)
                        .param(START_DATE_PARAM, START_DATE)
                        .param(END_DATE_PARAM, END_DATE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(RECORD_RESPONSE))));
    }
}
