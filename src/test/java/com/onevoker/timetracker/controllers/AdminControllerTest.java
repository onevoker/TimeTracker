package com.onevoker.timetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.controllers.security.annotations.WithAdminUser;
import com.onevoker.timetracker.controllers.security.annotations.WithDefaultUser;
import com.onevoker.timetracker.dto.ApiErrorResponse;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import com.onevoker.timetracker.exceptions.NotFoundException;
import com.onevoker.timetracker.security.services.RoleManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc api;

    @MockBean
    private RoleManagementService roleManagementService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ADD_ROLE_ENDPOINT = "/admin/add-role";
    private static final String CREATE_USER_WITH_ROLE_ENDPOINT = "/admin/create-user-with-role";

    private static final String USERNAME_PARAM = "username";
    private static final String ROLE_NAME_PARAM = "roleName";
    private static final String ROLE_ADMIN = "ROLE_Admin";
    private static final String ROLE_USER = "ROLE_User";
    private static final String EXIST_USER_USERNAME = "user";
    private static final String NEW_USER_USERNAME = "newUser";
    private static final String PASSWORD = "1111";
    private static final String CREATE_USER_RESPONSE = "%s was created with role %s. \n"
            + "P.S. If you created him with ROLE_Admin, that's mean, that he also have ROLE_User";
    private static final String ADD_ROLE_RESPONSE = "To %s was added role %s";

    @Test
    @WithAdminUser
    void testAddRoleToUserByAdmin() throws Exception {
        String expectedResult = ADD_ROLE_RESPONSE.formatted(EXIST_USER_USERNAME, ROLE_ADMIN);

        when(roleManagementService.addRoleToUser(EXIST_USER_USERNAME, ROLE_ADMIN))
                .thenReturn(expectedResult);

        api.perform(post(ADD_ROLE_ENDPOINT)
                        .param(USERNAME_PARAM, EXIST_USER_USERNAME)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }

    @Test
    @WithAdminUser
    void testAddRoleToUserByAdminThrowsNotFoundException() throws Exception {
        String errorMessage = "User not found";
        String expectedExceptionName = "NotFoundException";
        String expectedCode = "404 NOT_FOUND";

        when(roleManagementService.addRoleToUser(NEW_USER_USERNAME, ROLE_USER))
                .thenThrow(new NotFoundException(errorMessage));

        ApiErrorResponse expectedErrorResponse = ApiErrorResponse.builder()
                .exceptionName(expectedExceptionName)
                .exceptionMessage(errorMessage)
                .code(expectedCode)
                .build();

        String expectedResponseBody = objectMapper.writeValueAsString(expectedErrorResponse);

        api.perform(post(ADD_ROLE_ENDPOINT)
                        .param(USERNAME_PARAM, NEW_USER_USERNAME)
                        .param(ROLE_NAME_PARAM, ROLE_USER)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    void testAddRoleToUserByUnauthorizedUser() throws Exception {
        api.perform(post(ADD_ROLE_ENDPOINT)
                        .param(USERNAME_PARAM, EXIST_USER_USERNAME)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithDefaultUser
    void testAddRoleToUserByDefaultUser() throws Exception {
        api.perform(post(ADD_ROLE_ENDPOINT)
                        .param(USERNAME_PARAM, EXIST_USER_USERNAME)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // now start testing createUserWithRole method

    @Test
    @WithAdminUser
    void testCreateUserWithRoleByAdmin() throws Exception {
        AuthRequest authRequest = new AuthRequest(NEW_USER_USERNAME, PASSWORD);

        String expectedResult = CREATE_USER_RESPONSE.formatted(NEW_USER_USERNAME, ROLE_ADMIN);

        when(roleManagementService.createUserWithRole(any(AuthRequest.class), anyString()))
                .thenReturn(expectedResult);

        String requestBody = objectMapper.writeValueAsString(authRequest);

        api.perform(post(CREATE_USER_WITH_ROLE_ENDPOINT)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResult));
    }

    @Test
    @WithAdminUser
    void testCreateUserWithRoleByAdminThrowsDuplicateDataException() throws Exception {
        String errorMessage = "This user is registered, try another one username";

        String expectedExceptionMessage = "DuplicateDataException";
        String expectedCode = "409 CONFLICT";

        when(roleManagementService.createUserWithRole(any(AuthRequest.class), anyString()))
                .thenThrow(new DuplicateDataException(errorMessage));

        AuthRequest request = AuthRequest.builder()
                .username(EXIST_USER_USERNAME)
                .password(PASSWORD)
                .build();

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .exceptionName(expectedExceptionMessage)
                .exceptionMessage(errorMessage)
                .code(expectedCode)
                .build();

        String requestBody = objectMapper.writeValueAsString(request);
        String expectedResponseBody = objectMapper.writeValueAsString(expectedResponse);

        api.perform(post(CREATE_USER_WITH_ROLE_ENDPOINT)
                        .param(ROLE_NAME_PARAM, ROLE_USER)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    void testCreateUserWithRoleByUnauthorizedUser() throws Exception {
        AuthRequest authRequest = new AuthRequest(NEW_USER_USERNAME, PASSWORD);

        String requestBody = objectMapper.writeValueAsString(authRequest);

        api.perform(post(CREATE_USER_WITH_ROLE_ENDPOINT)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithDefaultUser
    void testCreateUserWithRoleByDefaultUser() throws Exception {
        AuthRequest authRequest = new AuthRequest(NEW_USER_USERNAME, PASSWORD);

        String requestBody = objectMapper.writeValueAsString(authRequest);

        api.perform(post(CREATE_USER_WITH_ROLE_ENDPOINT)
                        .param(ROLE_NAME_PARAM, ROLE_ADMIN)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
