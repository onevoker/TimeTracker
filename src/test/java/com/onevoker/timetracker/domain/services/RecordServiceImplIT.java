package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.repositories.RecordEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.domain.services.interfaces.RecordService;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.exceptions.UserNotInProjectException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class RecordServiceImplIT extends IntegrationTest {

    @Autowired
    private RecordService recordService;

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private RecordEntityRepository recordEntityRepository;

    private ProjectEntity projectEntity;
    private UserEntity userEntity;

    // Set up data
    private static final String PROJECT_NAME = "TimeTracker";
    private static final String PROJECT_DESCRIPTION = "Real cool project!";
    private static final String USER_USERNAME = "Onevoker";
    private static final String USER_PASSWORD = "reallyGoodPassword";
    private static final String RECORD_DESCRIPTION = "Worked on feature";
    private static final int RECORD_HOURS = 5;
    private static final RecordRequest RECORD_REQUEST = new RecordRequest(RECORD_HOURS, RECORD_DESCRIPTION);
    private static final RecordResponse EXPECTED_RECORD_RESPONSE = new RecordResponse(
            PROJECT_NAME,
            USER_USERNAME,
            RECORD_HOURS,
            RECORD_DESCRIPTION,
            OffsetDateTime.now()
    );

    // Messages from exceptions
    private static final String USER_NOT_IN_PROJECT_MESSAGE = "User does not belong to the project";

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
    void testCreateRecord() {
        projectEntity.getUserEntities().add(userEntity);
        projectEntityRepository.save(projectEntity);

        recordService.createRecord(RECORD_REQUEST, userEntity.getId(), projectEntity.getId());

        RecordEntity createdRecord = recordEntityRepository.findAll().getFirst();

        assertAll(
                () -> assertThat(createdRecord.getDescription()).isEqualTo(RECORD_DESCRIPTION),
                () -> assertThat(createdRecord.getHours()).isEqualTo(RECORD_HOURS)
        );
    }

    @Test
    void testCreateRecordThrowsUserNotInProjectException() {
        assertThatThrownBy(() -> recordService.createRecord(RECORD_REQUEST, userEntity.getId(), projectEntity.getId()))
                .isInstanceOf(UserNotInProjectException.class)
                .hasMessage(USER_NOT_IN_PROJECT_MESSAGE);
    }

    @Test
    void testGetAllRecords() {
        createRecordEntityForTest();

        List<RecordResponse> records = recordService.getAllRecords();

        assertAll(
                () -> assertThat(records).hasSize(1),
                () -> assertThat(recordsAreEqualsIgnoringCreatedAt(records.getFirst(), EXPECTED_RECORD_RESPONSE)).isTrue()
        );
    }

    @Test
    void testGetRecordById() {
        RecordEntity recordEntity = createRecordEntityForTest();

        RecordResponse actual = recordService.getRecordById(recordEntity.getId());

        assertThat(recordsAreEqualsIgnoringCreatedAt(actual, EXPECTED_RECORD_RESPONSE)).isTrue();
    }

    @Test
    void testUpdateRecord() {
        RecordEntity recordEntity = createRecordEntityForTest();

        int updatedHours = 10;
        String updatedDescription = "Kek";
        RecordRequest updatedRecordRequest = new RecordRequest(updatedHours, updatedDescription);

        recordService.updateRecord(recordEntity.getId(), updatedRecordRequest);

        RecordEntity updatedRecordEntity = recordEntityRepository.findById(recordEntity.getId()).orElse(null);

        assertAll(
                () -> assertThat(updatedRecordEntity.getDescription()).isEqualTo(updatedDescription),
                () -> assertThat(updatedRecordEntity.getHours()).isEqualTo(updatedHours)
        );
    }

    @Test
    void testDeleteRecord() {
        RecordEntity recordEntity = createRecordEntityForTest();

        recordService.deleteRecord(recordEntity.getId());

        RecordEntity deletedRecord = recordEntityRepository.findById(recordEntity.getId()).orElse(null);

        assertThat(deletedRecord).isNull();
    }

    @Test
    void testGetRecordsOnProjectBetweenDates() {
        createRecordEntityForTest();

        List<RecordResponse> records = recordService.getRecordsOnProjectBetweenDates(
                projectEntity.getId(),
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusDays(1)
        );

        assertAll(
                () -> assertThat(records).hasSize(1),
                () -> assertThat(recordsAreEqualsIgnoringCreatedAt(records.getFirst(), EXPECTED_RECORD_RESPONSE)).isTrue()
        );
    }

    @Test
    void testGetRecordsOnProjectForUserBetweenDates() {
        createRecordEntityForTest();

        List<RecordResponse> records = recordService.getRecordsOnProjectForUserBetweenDates(
                projectEntity.getId(),
                userEntity.getId(),
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusDays(1)
        );

        assertAll(
                () -> assertThat(records).hasSize(1),
                () -> assertThat(recordsAreEqualsIgnoringCreatedAt(records.getFirst(), EXPECTED_RECORD_RESPONSE)).isTrue()
        );
    }

    @Test
    void testGetUserRecordsAcrossProjectsBetweenDates() {
        createRecordEntityForTest();

        List<RecordResponse> records = recordService.getUserRecordsAcrossProjectsBetweenDates(
                userEntity.getId(),
                OffsetDateTime.now().minusDays(1),
                OffsetDateTime.now().plusDays(1)
        );

        assertAll(
                () -> assertThat(records).hasSize(1),
                () -> assertThat(recordsAreEqualsIgnoringCreatedAt(records.getFirst(), EXPECTED_RECORD_RESPONSE)).isTrue()
        );
    }

    @Test
    void testGetAllRecordsBetweenDates() {
        createRecordEntityForTest();
        OffsetDateTime now = OffsetDateTime.now();

        List<RecordResponse> records = recordService.getAllRecordsBetweenDates(now.minusDays(1), now.plusDays(1));

        assertAll(
                () -> assertThat(records).hasSize(1),
                () -> assertThat(recordsAreEqualsIgnoringCreatedAt(records.getFirst(), EXPECTED_RECORD_RESPONSE)).isTrue()
        );
    }

    private RecordEntity createRecordEntityForTest() {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setDescription(RECORD_DESCRIPTION);
        recordEntity.setHours(RECORD_HOURS);
        recordEntity.setUserEntity(userEntity);
        recordEntity.setProjectEntity(projectEntity);
        recordEntityRepository.save(recordEntity);

        return recordEntity;
    }

    private boolean recordsAreEqualsIgnoringCreatedAt(RecordResponse actual, RecordResponse expected) {
        return actual.projectName().equals(expected.projectName())
                && actual.username().equals(expected.username())
                && actual.hours() == expected.hours()
                && actual.description().equals(expected.description());
    }
}
