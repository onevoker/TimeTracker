package com.onevoker.timetracker.domain.services.entityFinder;

import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.RoleEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.repositories.RecordEntityRepository;
import com.onevoker.timetracker.domain.repositories.RoleEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@SpringBootTest
@Transactional
public class EntityFinderServiceIT extends IntegrationTest {
    @Autowired
    private EntityFinderService entityFinderService;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private RecordEntityRepository recordEntityRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    private ProjectEntity project;
    private UserEntity user;
    private RecordEntity record;
    private RoleEntity role;

    // Set up data
    private static final String PROJECT_NAME = "TimeTracker";
    private static final String PROJECT_DESCRIPTION = "Real cool project!";
    private static final String USER_USERNAME = "Onevoker";
    private static final String USER_PASSWORD = "reallyGoodPassword";
    private static final String EXISTS_ROLE = "ROLE_ADMIN";
    private static final String RECORD_DESCRIPTION = "I worked so hard!";
    private static final int hours = 8;

    // Messages from exceptions
    private static final String NO_USER_MESSAGE = "This user does not exist";
    private static final String NO_PROJECT_MESSAGE = "This project does not exist";
    private static final String NO_RECORD_MESSAGE = "This record does not exist";
    private static final String NO_ROLE_MESSAGE = "This role does not exist\n"
            + "P.S. Role should start with prefix ROLE_, for example: ROLE_Admin";

    // Not exists data
    private static final int NOT_EXISTS_ID = 999;
    private static final String NOT_EXISTS_USERNAME = "LordVoldemort";
    private static final String NOT_EXISTS_ROLE = "ROLE_Kayfarik";

    @BeforeEach
    void setUp() {
        project = new ProjectEntity();
        project.setName(PROJECT_NAME);
        project.setDescription(PROJECT_DESCRIPTION);
        projectEntityRepository.save(project);

        user = new UserEntity();
        user.setUsername(USER_USERNAME);
        user.setPassword(USER_PASSWORD);
        userEntityRepository.save(user);

        role = new RoleEntity();
        role.setName(EXISTS_ROLE);
        roleEntityRepository.save(role);

        record = new RecordEntity();
        record.setUserEntity(user);
        record.setProjectEntity(project);
        record.setHours(hours);
        record.setDescription(RECORD_DESCRIPTION);
        record.setCreatedAt(OffsetDateTime.now());
        recordEntityRepository.save(record);
    }

    @Test
    void testGetProjectEntity() {
        ProjectEntity result = entityFinderService.getProjectEntity(project.getId());

        assertThat(result).isEqualTo(project);
    }

    @Test
    void testGetProjectEntityThrowsNotFoundException() {
        assertThatThrownBy(() -> entityFinderService.getProjectEntity(NOT_EXISTS_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NO_PROJECT_MESSAGE);
    }

    @Test
    void testGetUserEntity() {
        UserEntity result = entityFinderService.getUserEntity(user.getId());

        assertThat(result).isEqualTo(user);
    }

    @Test
    void testGetUserEntityThrowsNotFoundException() {
        assertThatThrownBy(() -> entityFinderService.getUserEntity(NOT_EXISTS_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NO_USER_MESSAGE);
    }

    @Test
    void testGetUserEntityByUsername() {
        UserEntity result = entityFinderService.getUserEntity(USER_USERNAME);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void testGetUserEntityByUsernameThrowsNotFoundException() {
        assertThatThrownBy(() -> entityFinderService.getUserEntity(NOT_EXISTS_USERNAME))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NO_USER_MESSAGE);
    }

    @Test
    void testGetRecordEntity() {
        RecordEntity result = entityFinderService.getRecordEntity(record.getId());

        assertThat(result).isEqualTo(record);
    }

    @Test
    void testGetRecordEntityThrowsNotFoundException() {
        assertThatThrownBy(() -> entityFinderService.getRecordEntity(NOT_EXISTS_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NO_RECORD_MESSAGE);
    }

    @Test
    void testGetRoleEntity() {
        RoleEntity result = entityFinderService.getRoleEntity(EXISTS_ROLE);

        assertThat(result).isEqualTo(role);
    }

    @Test
    void testGetRoleEntityThrowsNotFoundException() {
        assertThatThrownBy(() -> entityFinderService.getRoleEntity(NOT_EXISTS_ROLE))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(NO_ROLE_MESSAGE);
    }
}
