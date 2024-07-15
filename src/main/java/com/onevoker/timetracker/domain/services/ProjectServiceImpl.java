package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.mappers.Mapper;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.domain.services.interfaces.ProjectService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import com.onevoker.timetracker.exceptions.SameProjectNameException;
import com.onevoker.timetracker.exceptions.UserInProjectException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final Mapper mapper;
    private final ProjectEntityRepository projectEntityRepository;
    private final EntityFinder entityFinder;

    private static final String DUPLICATE_MESSAGE = "This project was created before";
    private static final String SAME_PROJECT_NAMES_MESSAGE = "The name of the project is the same as its current name";
    private static final String USER_ALREADY_IN_PROJECT_MESSAGE = "This user is already in the project";


    @Override
    public void createProject(Project project) {
        if (projectEntityRepository.existsByName(project.name())) {
            throw new DuplicateDataException(DUPLICATE_MESSAGE);
        }
        ProjectEntity projectEntity = mapper.getProjectEntity(project);
        projectEntityRepository.save(projectEntity);
    }

    @Override
    public List<Project> getAllProjects() {
        return projectEntityRepository.findAll().stream()
                .map(mapper::getProject)
                .collect(Collectors.toList());
    }

    @Override
    public Project getProjectById(Integer id) {
        ProjectEntity projectEntity = entityFinder.getProjectEntity(id);
        return mapper.getProject(projectEntity);
    }

    @Override
    public List<User> getAllUsersOnProject(Integer id) {
        ProjectEntity projectEntity = entityFinder.getProjectEntity(id);
        return projectEntity.getUserEntities().stream()
                .map(mapper::getUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProject(Integer id, Project project) {
        ProjectEntity existProject = entityFinder.getProjectEntity(id);
        try {
            if (existProject.getDescription().equals(project.description())) {
                if (existProject.getName().equals(project.name())) {
                    throw new SameProjectNameException(SAME_PROJECT_NAMES_MESSAGE);
                }
            }
            existProject.setName(project.name());
            existProject.setDescription(project.description());
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateDataException(DUPLICATE_MESSAGE);
        }
    }

    @Override
    public void deleteProject(Integer id) {
        projectEntityRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addUserToProject(Integer userId, Integer projectId) {
        ProjectEntity projectEntity = entityFinder.getProjectEntity(projectId);
        UserEntity userEntity = entityFinder.getUserEntity(userId);

        if (projectEntity.getUserEntities().contains(userEntity)) {
            throw new UserInProjectException(USER_ALREADY_IN_PROJECT_MESSAGE);
        }

        projectEntity.getUserEntities().add(userEntity);
    }

    @Override
    @Transactional
    public void deleteUserFromProject(Integer userId, Integer projectId) {
        ProjectEntity projectEntity = entityFinder.getProjectEntity(projectId);
        UserEntity userEntity = entityFinder.getUserEntity(userId);

        projectEntity.getUserEntities().remove(userEntity);
    }
}
