package com.onevoker.timetracker.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.controllers.security.annotations.WithAdminUser;
import com.onevoker.timetracker.controllers.security.annotations.WithDefaultUser;
import com.onevoker.timetracker.domain.services.interfaces.ProjectService;
import com.onevoker.timetracker.dto.ApiErrorResponse;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
class ProjectControllerTest extends IntegrationTest {
    @Autowired
    private MockMvc api;

    @MockBean
    private ProjectService projectService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PROJECTS_ENDPOINT = "/projects";
    private static final String PROJECT_ID_PARAM = "/{id}";
    private static final String USERS_ON_PROJECT_ENDPOINT = "/{id}/users";
    private static final String ADD_USER_TO_PROJECT_ENDPOINT = "/{projectId}/users/{userId}";

    private static final Integer PROJECT_ID = 1;
    private static final Integer USER_ID = 1;
    private static final Project PROJECT = new Project("Test Project", "Test Description");
    private static final User USER = new User("Test User");

    private static final ApiErrorResponse UNAUTHORIZED_RESPONSE = ApiErrorResponse.builder()
            .exceptionName("InsufficientAuthenticationException")
            .exceptionMessage("Full authentication is required to access this resource")
            .code("401 UNAUTHORIZED")
            .build();

    @Test
    @WithAdminUser
    void testCreateProject() throws Exception {
        doNothing().when(projectService).createProject(any(Project.class));

        String requestBody = objectMapper.writeValueAsString(PROJECT);

        api.perform(post(PROJECTS_ENDPOINT)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateProjectUnauthorized() throws Exception {
        String requestBody = objectMapper.writeValueAsString(PROJECT);

        api.perform(post(PROJECTS_ENDPOINT)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testCreateProjectForbidden() throws Exception {
        String requestBody = objectMapper.writeValueAsString(PROJECT);

        api.perform(post(PROJECTS_ENDPOINT)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithDefaultUser
    void testGetAllProjects() throws Exception {
        List<Project> projects = List.of(PROJECT);
        when(projectService.getAllProjects()).thenReturn(projects);

        api.perform(get(PROJECTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projects)));
    }

    @Test
    void testGetAllProjectsUnauthorized() throws Exception {
        api.perform(get(PROJECTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testGetProjectById() throws Exception {
        when(projectService.getProjectById(anyInt())).thenReturn(PROJECT);

        api.perform(get(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(PROJECT)));
    }

    @Test
    void testGetProjectByIdUnauthorized() throws Exception {
        api.perform(get(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testGetAllUsersOnProject() throws Exception {
        List<User> users = List.of(USER);
        when(projectService.getAllUsersOnProject(anyInt())).thenReturn(users);

        api.perform(get(PROJECTS_ENDPOINT + USERS_ON_PROJECT_ENDPOINT, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
    }

    @Test
    void testGetAllUsersOnProjectUnauthorized() throws Exception {
        api.perform(get(PROJECTS_ENDPOINT + USERS_ON_PROJECT_ENDPOINT, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithAdminUser
    void testUpdateProject() throws Exception {
        doNothing().when(projectService).updateProject(anyInt(), any(Project.class));

        String requestBody = objectMapper.writeValueAsString(PROJECT);

        api.perform(put(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProjectUnauthorized() throws Exception {
        String requestBody = objectMapper.writeValueAsString(PROJECT);

        api.perform(put(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithAdminUser
    void testDeleteProject() throws Exception {
        doNothing().when(projectService).deleteProject(anyInt());

        api.perform(delete(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteProjectUnauthorized() throws Exception {
        api.perform(delete(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testDeleteProjectForbidden() throws Exception {
        api.perform(delete(PROJECTS_ENDPOINT + PROJECT_ID_PARAM, PROJECT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAdminUser
    void testAddUserToProject() throws Exception {
        doNothing().when(projectService).addUserToProject(anyInt(), anyInt());

        api.perform(post(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testAddUserToProjectUnauthorized() throws Exception {
        api.perform(post(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testAddUserToProjectForbidden() throws Exception {
        api.perform(post(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAdminUser
    void testRemoveUserFromProject() throws Exception {
        doNothing().when(projectService).deleteUserFromProject(anyInt(), anyInt());

        api.perform(delete(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveUserFromProjectUnauthorized() throws Exception {
        api.perform(delete(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(objectMapper.writeValueAsString(UNAUTHORIZED_RESPONSE)));
    }

    @Test
    @WithDefaultUser
    void testRemoveUserFromProjectForbidden() throws Exception {
        api.perform(delete(PROJECTS_ENDPOINT + ADD_USER_TO_PROJECT_ENDPOINT, PROJECT_ID, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
