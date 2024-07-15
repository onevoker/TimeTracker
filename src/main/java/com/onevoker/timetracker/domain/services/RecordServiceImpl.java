package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.mappers.Mapper;
import com.onevoker.timetracker.domain.repositories.RecordEntityRepository;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.domain.services.interfaces.RecordService;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.exceptions.UserNotInProjectException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final Mapper mapper;
    private final RecordEntityRepository recordEntityRepository;
    private final EntityFinder entityFinder;

    private static final String USER_NOT_IN_PROJECT_MESSAGE = "User does not belong to the project";

    @Override
    public void createRecord(RecordRequest recordRequest, Integer userId, Integer projectId) {
        UserEntity userEntity = entityFinder.getUserEntity(userId);
        ProjectEntity projectEntity = entityFinder.getProjectEntity(projectId);

        if (!projectEntity.getUserEntities().contains(userEntity)) {
            throw new UserNotInProjectException(USER_NOT_IN_PROJECT_MESSAGE);
        }

        RecordEntity recordEntity = mapper.getRecordEntity(recordRequest);
        recordEntity.setUserEntity(userEntity);
        recordEntity.setProjectEntity(projectEntity);
        recordEntityRepository.save(recordEntity);
    }

    @Override
    public List<RecordResponse> getAllRecords() {
        return recordEntityRepository.findAll().stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RecordResponse getRecordById(Integer id) {
        RecordEntity recordEntity = entityFinder.getRecordEntity(id);
        return mapper.getRecordResponse(recordEntity);
    }

    @Override
    @Transactional
    public void updateRecord(Integer id, RecordRequest recordRequest) {
        RecordEntity existingRecordEntity = entityFinder.getRecordEntity(id);

        existingRecordEntity.setHours(recordRequest.hours());
        existingRecordEntity.setDescription(recordRequest.description());
    }

    @Override
    public void deleteRecord(Integer id) {
        recordEntityRepository.deleteById(id);
    }

    @Override
    public List<RecordResponse> getRecordsOnProjectBetweenDates(Integer projectId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return recordEntityRepository.findByProjectEntityIdAndCreatedAtBetween(projectId, startDate, endDate).stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getRecordsOnProjectForUserBetweenDates(Integer projectId, Integer userId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return recordEntityRepository.findByProjectEntityIdAndUserEntityIdAndCreatedAtBetween(projectId, userId, startDate, endDate).stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getUserRecordsAcrossProjectsBetweenDates(Integer userId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return recordEntityRepository.findByUserEntityIdAndCreatedAtBetween(userId, startDate, endDate).stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getAllRecordsBetweenDates(OffsetDateTime startDate, OffsetDateTime endDate) {
        return recordEntityRepository.findByCreatedAtBetween(startDate, endDate).stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }
}
