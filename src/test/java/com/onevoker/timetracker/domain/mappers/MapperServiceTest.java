package com.onevoker.timetracker.domain.mappers;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MapperServiceTest {
    private static final MapperService mapperService = new MapperService();

    private static final String PROJECT_NAME = "TimeTracker";
    private static final String PROJECT_DESCRIPTION = "api for time tracking)";
    private static final String USERNAME = "coolGuy";
    private static final String USER_PASSWORD = "veryCoolPasswordOfCoolGuy";
    private static final int WORK_HOURS = 10;
    private static final String RECORD_DESCRIPTION = "I worked so hard on it, now all works!";

    @Test
    void testGetUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(USERNAME);
        userEntity.setPassword(USER_PASSWORD);

        User result = mapperService.getUser(userEntity);
        User expected = new User(USERNAME);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testGetProjectEntity() {
        Project project = new Project(PROJECT_NAME, PROJECT_DESCRIPTION);

        ProjectEntity result = mapperService.getProjectEntity(project);


        assertAll(
                () -> assertThat(result.getName()).isEqualTo(PROJECT_NAME),
                () -> assertThat(result.getDescription()).isEqualTo(PROJECT_DESCRIPTION)
        );
    }

    @Test
    void testGetProject() {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(PROJECT_NAME);
        projectEntity.setDescription(PROJECT_DESCRIPTION);

        Project result = mapperService.getProject(projectEntity);
        Project expected = new Project(PROJECT_NAME, PROJECT_DESCRIPTION);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testGetRecordResponse() {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(PROJECT_NAME);
        projectEntity.setDescription(PROJECT_DESCRIPTION);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(USERNAME);
        userEntity.setPassword(USER_PASSWORD);

        OffsetDateTime time = OffsetDateTime.now();

        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setDescription(RECORD_DESCRIPTION);
        recordEntity.setCreatedAt(time);
        recordEntity.setHours(WORK_HOURS);
        recordEntity.setUserEntity(userEntity);
        recordEntity.setProjectEntity(projectEntity);

        RecordResponse result = mapperService.getRecordResponse(recordEntity);
        RecordResponse expected = new RecordResponse(PROJECT_NAME, USERNAME, WORK_HOURS, RECORD_DESCRIPTION, time);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testGetRecordEntity() {
        RecordRequest recordRequest = new RecordRequest(WORK_HOURS, RECORD_DESCRIPTION);

        RecordEntity result = mapperService.getRecordEntity(recordRequest);

        assertAll(
                () -> assertThat(result.getHours()).isEqualTo(WORK_HOURS),
                () -> assertThat(result.getDescription()).isEqualTo(RECORD_DESCRIPTION)
        );
    }
}
