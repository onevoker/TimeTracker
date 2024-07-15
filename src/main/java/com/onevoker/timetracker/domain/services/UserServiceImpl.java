package com.onevoker.timetracker.domain.services;

import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.mappers.Mapper;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.domain.services.interfaces.UserService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserEntityRepository userEntityRepository;
    private final EntityFinder entityFinder;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;

    private static final String SAME_USERNAME_MESSAGE = "This username is already in use";

    @Override
    public List<User> getAllUsers() {
        return userEntityRepository.findAll().stream()
                .map(mapper::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Integer id) {
        UserEntity userEntity = entityFinder.getUserEntity(id);
        return mapper.getUser(userEntity);
    }

    @Override
    public List<RecordResponse> getAllRecordsById(Integer id) {
        UserEntity userEntity = entityFinder.getUserEntity(id);
        return userEntity.getRecordEntities().stream()
                .map(mapper::getRecordResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> getAllProjectsById(Integer id) {
        UserEntity userEntity = entityFinder.getUserEntity(id);
        return userEntity.getProjectEntities().stream()
                .map(mapper::getProject)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUser(Integer id, AuthRequest request) {
        if (userEntityRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateDataException(SAME_USERNAME_MESSAGE);
        }
        UserEntity userEntity = entityFinder.getUserEntity(id);
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    @Override
    public void deleteUser(Integer id) {
        userEntityRepository.deleteById(id);
    }
}
