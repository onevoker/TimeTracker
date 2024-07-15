package com.onevoker.timetracker.security.services;

import com.onevoker.timetracker.domain.entities.RoleEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.domain.services.entityFinder.EntityFinder;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleManagementService {
    private final EntityFinder entityFinder;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userEntityRepository;

    private static final String ROLE_ADMIN = "ROLE_Admin";
    private static final String ROLE_USER = "ROLE_User";

    private static final String USER_EXIST_MESSAGE = "This user is registered, try another one username";
    private static final String SAME_ROLES_MESSAGE = "This user have this role!";
    private static final String ADD_ROLE_RESPONSE = "To %s was added role %s";
    private static final String CREATE_USER_RESPONSE = "%s was created with role %s. "
            + "P.S. If you created him with ROLE_Admin, that's mean, that he also have ROLE_User";

    @Transactional
    public String addRoleToUser(String username, String roleName) {
        UserEntity user = entityFinder.getUserEntity(username);
        RoleEntity role = entityFinder.getRoleEntity(roleName);

        if (user.getRoleEntities().contains(role)) {
            return SAME_ROLES_MESSAGE;
        }

        user.getRoleEntities().add(role);

        return ADD_ROLE_RESPONSE.formatted(username, roleName);
    }

    /***
     * Create user with role.
     * If {@code roleName} is ROLE_Admin, user will have also Role_User.
     * @throws DuplicateDataException
     *          If request have already existing username.
     */
    @Transactional
    public String createUserWithRole(AuthRequest authRequest, String roleName) {
        String username = authRequest.getUsername();

        if (userEntityRepository.findByUsername(username).isPresent()) {
            throw new DuplicateDataException(USER_EXIST_MESSAGE);
        }

        RoleEntity role = entityFinder.getRoleEntity(roleName);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        if (roleName.equals(ROLE_ADMIN)) {
            RoleEntity roleUser = entityFinder.getRoleEntity(ROLE_USER);
            user.setRoleEntities(List.of(role, roleUser));
        } else {
            user.setRoleEntities(List.of(role));
        }

        userEntityRepository.save(user);

        return CREATE_USER_RESPONSE.formatted(username, roleName);
    }
}
