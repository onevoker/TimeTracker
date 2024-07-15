package com.onevoker.timetracker.domain.services.interfaces;

import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.User;

import java.util.List;

public interface ProjectService {
    void createProject(Project project);

    List<Project> getAllProjects();

    Project getProjectById(Integer id);

    List<User> getAllUsersOnProject(Integer id);

    void updateProject(Integer id, Project project);

    void deleteProject(Integer id);

    void addUserToProject(Integer userId, Integer projectId);

    void deleteUserFromProject(Integer userId, Integer projectId);
}
