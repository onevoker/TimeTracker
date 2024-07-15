package com.onevoker.timetracker.domain.services.entityFinder;

import com.onevoker.timetracker.domain.entities.ProjectEntity;
import com.onevoker.timetracker.domain.entities.RecordEntity;
import com.onevoker.timetracker.domain.entities.RoleEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.ProjectEntityRepository;
import com.onevoker.timetracker.domain.repositories.RecordEntityRepository;
import com.onevoker.timetracker.domain.repositories.RoleEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/***
 * Service for find entities in database.
 * Methods throws NotFoundException — if entity not exists
 */
@Service
@RequiredArgsConstructor
public class EntityFinderService implements EntityFinder {
    private final ProjectEntityRepository projectEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final RecordEntityRepository recordEntityRepository;
    private final RoleEntityRepository roleEntityRepository;

    private static final String NO_USER_MESSAGE = "This user does not exist";
    private static final String NO_PROJECT_MESSAGE = "This project does not exist";
    private static final String NO_RECORD_MESSAGE = "This record does not exist";
    private static final String NO_ROLE_MESSAGE = "This role does not exist";
    private static final String ROLE_CREATION_GUIDE = "P.S. Role should start with prefix ROLE_, for example: ROLE_Admin";

    @Override
    public ProjectEntity getProjectEntity(Integer projectId) {
        return projectEntityRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(NO_PROJECT_MESSAGE));
    }

    @Override
    public UserEntity getUserEntity(Integer userId) {
        return userEntityRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NO_USER_MESSAGE));
    }

    @Override
    public UserEntity getUserEntity(String username) {
        return userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(NO_USER_MESSAGE));
    }

    @Override
    public RecordEntity getRecordEntity(Integer recordId) {
        return recordEntityRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException(NO_RECORD_MESSAGE));
    }

    @Override
    public RoleEntity getRoleEntity(String roleName) {
        return roleEntityRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException(NO_ROLE_MESSAGE + "\n" + ROLE_CREATION_GUIDE));
    }
}
