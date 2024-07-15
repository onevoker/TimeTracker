package com.onevoker.timetracker.domain.services.entityFinder;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.RoleEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;

public interface EntityFinder {
    ProjectEntity getProjectEntity(Integer projectId);

    UserEntity getUserEntity(Integer userId);

    UserEntity getUserEntity(String username);

    RecordEntity getRecordEntity(Integer recordId);

    RoleEntity getRoleEntity(String roleName);
}
