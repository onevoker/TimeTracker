package com.onevoker.timetracker.controllers;

import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.security.services.RoleManagementService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_Admin')")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {
    private final RoleManagementService roleManagementService;

    @PostMapping("/add-role")
    public String addRoleToUser(@RequestParam String username, @RequestParam String roleName) {
        return roleManagementService.addRoleToUser(username, roleName);
    }

    @PostMapping("/create-user-with-role")
    public String createUserWithRole(@RequestBody AuthRequest authRequest, @RequestParam String roleName) {
        return roleManagementService.createUserWithRole(authRequest, roleName);
    }
}
