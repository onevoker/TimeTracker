package com.onevoker.timetracker.domain.mappers;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordRequest;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import org.springframework.stereotype.Service;

@Service
public class MapperService implements Mapper {

    @Override
    public User getUser(UserEntity userEntity) {
        return new User(userEntity.getUsername());
    }

    @Override
    public ProjectEntity getProjectEntity(Project project) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(project.name());
        projectEntity.setDescription(project.description());
        return projectEntity;
    }

    @Override
    public Project getProject(ProjectEntity projectEntity) {
        return new Project(projectEntity.getName(), projectEntity.getDescription());
    }

    @Override
    public RecordResponse getRecordResponse(RecordEntity recordEntity) {
        return new RecordResponse(
                recordEntity.getProjectEntity().getName(),
                recordEntity.getUserEntity().getUsername(),
                recordEntity.getHours(),
                recordEntity.getDescription(),
                recordEntity.getCreatedAt()
        );
    }

    @Override
    public RecordEntity getRecordEntity(RecordRequest recordRequest) {
        RecordEntity recordEntity = new RecordEntity();
        recordEntity.setHours(recordRequest.hours());
        recordEntity.setDescription(recordRequest.description());
        return recordEntity;
    }
}
