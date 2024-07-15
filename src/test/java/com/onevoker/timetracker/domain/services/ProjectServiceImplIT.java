package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.domain.services.interfaces.ProjectService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.exceptions.SameProjectNameException;
import com.onevoker.timetracker.exceptions.UserInProjectException;
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
public class ProjectServiceImplIT extends IntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    private ProjectEntity projectEntity;
    private UserEntity userEntity;

    // Set up data
    private static final String PROJECT_NAME = "TimeTracker";
    private static final String PROJECT_DESCRIPTION = "Real cool project!";
    private static final Project PROJECT = new Project(PROJECT_NAME, PROJECT_DESCRIPTION);
    private static final String USER_USERNAME = "Onevoker";
    private static final String USER_PASSWORD = "reallyGoodPassword";
    private static final String NEW_PROJECT_NAME = "NewTimeTracker";
    private static final String NEW_PROJECT_DESCRIPTION = "Even cooler project!";

    // Messages from exceptions
    private static final String USER_ALREADY_IN_PROJECT_MESSAGE = "This user is already in the project";
    private static final String SAME_PROJECT_NAMES_MESSAGE = "The name of the project is the same as its current name";
    private static final String WAS_CREATED_MESSAGE = "This project was created before";

    @BeforeEach
    void setUp() {
        projectEntity = new ProjectEntity();
        projectEntity.setName(PROJECT_NAME);
        projectEntity.setDescription(PROJECT_DESCRIPTION);
        projectEntityRepository.save(projectEntity);

        userEntity = new UserEntity();
        userEntity.setUsername(USER_USERNAME);
        userEntity.setPassword(USER_PASSWORD);
        userEntityRepository.save(userEntity);
    }

    @Test
    void testCreateProject() {
        Project newProject = new Project(NEW_PROJECT_NAME, NEW_PROJECT_DESCRIPTION);

        projectService.createProject(newProject);

        ProjectEntity createdProject = projectEntityRepository.findByName(NEW_PROJECT_NAME).orElse(null);

        assertAll(
                () -> assertThat(createdProject.getName()).isEqualTo(NEW_PROJECT_NAME),
                () -> assertThat(createdProject.getDescription()).isEqualTo(NEW_PROJECT_DESCRIPTION)
        );
    }

    @Test
    void testCreateProjectThrowsWasCreatedException() {
        assertThatThrownBy(() -> projectService.createProject(PROJECT))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessage(WAS_CREATED_MESSAGE);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        int expectedSize = 1;

        assertAll(
                () -> assertThat(projects).hasSize(expectedSize),
                () -> {
                    Project foundProject = projects.getFirst();
                    assertThat(foundProject).isEqualTo(PROJECT);
                }
        );
    }

    @Test
    void testGetProjectById() {
        Project foundProject = projectService.getProjectById(projectEntity.getId());

        assertAll(
                () -> assertThat(foundProject.name()).isEqualTo(PROJECT_NAME),
                () -> assertThat(foundProject.description()).isEqualTo(PROJECT_DESCRIPTION)
        );
    }

    @Test
    void testGetAllUsersOnProject() {
        projectEntity.getUserEntities().add(userEntity);
        projectEntityRepository.save(projectEntity);

        List<User> users = projectService.getAllUsersOnProject(projectEntity.getId());
        int expectedSize = 1;

        assertAll(
                () -> assertThat(users).hasSize(expectedSize),
                () -> assertThat(users.getFirst().username()).isEqualTo(USER_USERNAME)
        );
    }

    @Test
    void testUpdateProject() {
        Project updatedProject = new Project(NEW_PROJECT_NAME, NEW_PROJECT_DESCRIPTION);

        projectService.updateProject(projectEntity.getId(), updatedProject);

        ProjectEntity updatedProjectEntity = projectEntityRepository.findById(projectEntity.getId()).orElse(null);

        assertAll(
                () -> assertThat(updatedProjectEntity.getName()).isEqualTo(NEW_PROJECT_NAME),
                () -> assertThat(updatedProjectEntity.getDescription()).isEqualTo(NEW_PROJECT_DESCRIPTION)
        );
    }

    @Test
    void testUpdateProjectThrowsSameProjectNameException() {
        assertThatThrownBy(() -> projectService.updateProject(projectEntity.getId(), PROJECT))
                .isInstanceOf(SameProjectNameException.class)
                .hasMessage(SAME_PROJECT_NAMES_MESSAGE);
    }

    @Test
    void testDeleteProject() {
        projectService.deleteProject(projectEntity.getId());

        ProjectEntity deletedProject = projectEntityRepository.findById(projectEntity.getId()).orElse(null);

        assertThat(deletedProject).isNull();
    }

    @Test
    void testAddUserToProject() {
        projectService.addUserToProject(userEntity.getId(), projectEntity.getId());

        ProjectEntity updatedProjectEntity = projectEntityRepository.findById(projectEntity.getId()).orElse(null);

        assertThat(updatedProjectEntity.getUserEntities()).contains(userEntity);
    }

    @Test
    void testAddUserToProjectThrowsUserInProjectException() {
        projectEntity.getUserEntities().add(userEntity);
        projectEntityRepository.save(projectEntity);

        assertThatThrownBy(() -> projectService.addUserToProject(userEntity.getId(), projectEntity.getId()))
                .isInstanceOf(UserInProjectException.class)
                .hasMessage(USER_ALREADY_IN_PROJECT_MESSAGE);
    }

    @Test
    void testDeleteUserFromProject() {
        projectEntity.getUserEntities().add(userEntity);
        projectEntityRepository.save(projectEntity);

        projectService.deleteUserFromProject(userEntity.getId(), projectEntity.getId());

        ProjectEntity updatedProjectEntity = projectEntityRepository.findById(projectEntity.getId()).orElse(null);

        assertThat(updatedProjectEntity.getUserEntities()).doesNotContain(userEntity);
    }
}
