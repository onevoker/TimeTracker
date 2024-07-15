package com.onevoker.timetracker.domain.services.interfaces;

import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;

import java.time.OffsetDateTime;
import java.util.List;

public interface RecordService {
    void createRecord(RecordRequest recordRequest, Integer userId, Integer projectId);

    List<RecordResponse> getAllRecords();

    RecordResponse getRecordById(Integer id);

    void updateRecord(Integer id, RecordRequest recordRequest);

    void deleteRecord(Integer id);

    List<RecordResponse> getRecordsOnProjectBetweenDates(Integer projectId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<RecordResponse> getRecordsOnProjectForUserBetweenDates(Integer projectId, Integer userId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<RecordResponse> getUserRecordsAcrossProjectsBetweenDates(Integer userId, OffsetDateTime startDate, OffsetDateTime endDate);

    List<RecordResponse> getAllRecordsBetweenDates(OffsetDateTime startDate, OffsetDateTime endDate);
}
