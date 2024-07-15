package com.onevoker.timetracker.domain.repositories;

import com.onevoker.timetracker.domain.entities.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface RecordEntityRepository extends JpaRepository<RecordEntity, Integer> {
    List<RecordEntity> findByProjectEntityIdAndUserEntityIdAndCreatedAtBetween(Integer projectId,
                                                                               Integer userId,
                                                                               OffsetDateTime startDate,
                                                                               OffsetDateTime endDate);

    List<RecordEntity> findByUserEntityIdAndCreatedAtBetween(Integer userId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<RecordEntity> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);

    List<RecordEntity> findByProjectEntityIdAndCreatedAtBetween(Integer projectId, OffsetDateTime startDate, OffsetDateTime endDate);
}