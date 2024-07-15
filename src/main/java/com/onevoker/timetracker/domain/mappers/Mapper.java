package com.onevoker.timetracker.domain.mappers;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;

/***
 * Service for mapping DTO with Entities
 */
public interface Mapper {
    User getUser(UserEntity userEntity);

    ProjectEntity getProjectEntity(Project project);

    Project getProject(ProjectEntity projectEntity);

    RecordResponse getRecordResponse(RecordEntity recordEntity);

    RecordEntity getRecordEntity(RecordRequest recordRequest);
}
