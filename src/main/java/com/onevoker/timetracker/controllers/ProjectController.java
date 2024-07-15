package com.onevoker.timetracker.controllers;

import com.onevoker.timetracker.domain.services.interfaces.ProjectService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.User;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Admin')")
    public void createProject(@RequestBody Project project) {
        projectService.createProject(project);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_User')")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public Project getProjectById(@PathVariable Integer id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<User> getAllUsersOnProject(@PathVariable Integer id) {
        return projectService.getAllUsersOnProject(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public void updateProject(@PathVariable Integer id, @RequestBody Project project) {
        projectService.updateProject(id, project);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public void deleteProject(@PathVariable Integer id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public void addUserToProject(@PathVariable Integer projectId, @PathVariable Integer userId) {
        projectService.addUserToProject(userId, projectId);
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_Admin')")
    public void removeUserFromProject(@PathVariable Integer projectId, @PathVariable Integer userId) {
        projectService.deleteUserFromProject(userId, projectId);
    }
}
