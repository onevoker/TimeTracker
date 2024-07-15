package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.repositories.RecordEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class UserServiceImplIT extends IntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    private RecordServiceImpl recordService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private RecordEntityRepository recordEntityRepository;

    private int existingUserId;

    // Set up data
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password";
    private static final String PROJECT_NAME = "Test Project";
    private static final String PROJECT_DESCRIPTION = "Test Project Description";
    private static final String RECORD_DESCRIPTION = "Test Record";
    private static final int RECORD_HOURS = 5;
    private static final User EXISTING_USER = new User(USERNAME);

    // Messages from exceptions
    private static final String WAS_CREATED_EXCEPTION_MESSAGE = "This username is already in use";

    @BeforeEach
    void setUp() {
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername(USERNAME);
        existingUser.setPassword(PASSWORD);
        userEntityRepository.save(existingUser);
        existingUserId = existingUser.getId();

        projectService.createProject(new Project(PROJECT_NAME, PROJECT_DESCRIPTION));
        ProjectEntity existingProject = projectEntityRepository.findAll().getFirst();
        int projectId = existingProject.getId();
        projectService.addUserToProject(existingUserId, projectId);

        RecordRequest recordRequest = new RecordRequest(RECORD_HOURS, RECORD_DESCRIPTION);
        recordService.createRecord(recordRequest, existingUserId, projectId);
        RecordEntity existingRecord = recordEntityRepository.findAll().getFirst();

        existingUser.getProjectEntities().add(existingProject);
        existingUser.getRecordEntities().add(existingRecord);
        userEntityRepository.save(existingUser);
    }

    @Test
    void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertThat(users).contains(EXISTING_USER);
    }

    @Test
    void testGetUserById() {
        User user = userService.getUserById(existingUserId);
        assertThat(user).isEqualTo(EXISTING_USER);
    }

    @Test
    void testGetAllRecordsById() {
        List<RecordResponse> records = userService.getAllRecordsById(existingUserId);

        RecordResponse record = records.getFirst();

        assertAll(
                () -> assertThat(record.description()).isEqualTo(RECORD_DESCRIPTION),
                () -> assertThat(record.hours()).isEqualTo(RECORD_HOURS),
                () -> assertThat(record.username()).isEqualTo(USERNAME),
                () -> assertThat(record.projectName()).isEqualTo(PROJECT_NAME)
        );
    }

    @Test
    void testGetAllProjectsById() {
        List<Project> projects = userService.getAllProjectsById(existingUserId);

        Project project = projects.getFirst();

        assertThat(project).isEqualTo(new Project(PROJECT_NAME, PROJECT_DESCRIPTION));
    }

    @Test
    void testUpdateUser() {
        String newUsername = "newUserName";
        AuthRequest request = new AuthRequest(newUsername, PASSWORD);
        userService.updateUser(existingUserId, request);

        UserEntity updatedUser = userEntityRepository.findById(existingUserId).orElse(null);

        assertThat(updatedUser.getUsername()).isEqualTo(newUsername);
    }

    @Test
    void testUpdateUserThrowsWasCreatedException() {
        AuthRequest request = new AuthRequest(USERNAME, PASSWORD);

        assertThatThrownBy(() -> userService.updateUser(existingUserId, request))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessage(WAS_CREATED_EXCEPTION_MESSAGE);
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(existingUserId);

        UserEntity deletedUser = userEntityRepository.findById(existingUserId).orElse(null);

        assertThat(deletedUser).isNull();
    }
}
