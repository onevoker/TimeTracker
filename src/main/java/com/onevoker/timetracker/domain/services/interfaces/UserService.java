package com.onevoker.timetracker.domain.services.interfaces;

import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.dto.auth.AuthRequest;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer id);

    List<RecordResponse> getAllRecordsById(Integer id);

    List<Project> getAllProjectsById(Integer id);

    void updateUser(Integer id, AuthRequest request);

    void deleteUser(Integer id);
}
