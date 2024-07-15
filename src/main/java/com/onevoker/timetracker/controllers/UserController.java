package com.onevoker.timetracker.controllers;

import com.onevoker.timetracker.domain.services.interfaces.UserService;
import com.onevoker.timetracker.dto.Project;
import com.onevoker.timetracker.dto.RecordResponse;
import com.onevoker.timetracker.dto.User;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.security.services.VerifyService;
import com.onevoker.timetracker.security.userPrincipal.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final VerifyService verifyService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_User')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/records")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<RecordResponse> getUserRecords(@PathVariable Integer id) {
        return userService.getAllRecordsById(id);
    }

    @GetMapping("/{id}/projects")
    @PreAuthorize("hasRole('ROLE_User')")
    public List<Project> getUserProjects(@PathVariable Integer id) {
        return userService.getAllProjectsById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public void updateUser(@PathVariable Integer id, @RequestBody AuthRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        verifyService.verifyUserId(id, principal);
        userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_User')")
    public void deleteUser(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal) {
        verifyService.verifyUserIdOrAdmin(id, principal);
        userService.deleteUser(id);
    }
}
