package com.onevoker.timetracker.security.services;

import com.onevoker.timetracker.IntegrationTest;
import com.onevoker.timetracker.domain.entities.RoleEntity;
import com.onevoker.timetracker.domain.entities.UserEntity;
import com.onevoker.timetracker.domain.repositories.RoleEntityRepository;
import com.onevoker.timetracker.domain.repositories.UserEntityRepository;
import com.onevoker.timetracker.dto.auth.AuthRequest;
import com.onevoker.timetracker.exceptions.DuplicateDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class RoleManagementServiceIT extends IntegrationTest {
    @Autowired
    private RoleManagementService roleManagementService;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private RoleEntityRepository roleEntityRepository;

    // Set up data
    private static final String ROLE_ADMIN = "ROLE_Admin";
    private static final String ROLE_USER = "ROLE_User";

    private static final String USER_USERNAME = "testuser";
    private static final String USER_PASSWORD = "password";
    private static final String NEW_USER_USERNAME = "newuser";
    private static final String NEW_USER_PASSWORD = "newpassword";

    // Messages from exceptions
    private static final String USER_EXIST_MESSAGE = "This user is registered, try another one username";
    private static final String SAME_ROLES_MESSAGE = "This user have this role!";
    private static final String ADD_ROLE_RESPONSE = "To %s was added role %s";
    private static final String CREATE_USER_RESPONSE = "%s was created with role %s. "
            + "P.S. If you created him with ROLE_Admin, that's mean, that he also have ROLE_User";

    private RoleEntity adminRoleEntity;
    private RoleEntity userRoleEntity;

    @BeforeEach
    void setUp() {
        adminRoleEntity = roleEntityRepository.findByName(ROLE_ADMIN).get();
        userRoleEntity = roleEntityRepository.findByName(ROLE_USER).get();
    }

    @Test
    void testAddRoleToUser() {
        UserEntity user = createTestUser();

        String result = roleManagementService.addRoleToUser(USER_USERNAME, ROLE_ADMIN);

        assertAll(
                () -> assertThat(result).isEqualTo(String.format(ADD_ROLE_RESPONSE, USER_USERNAME, ROLE_ADMIN)),
                () -> assertThat(user.getRoleEntities()).contains(adminRoleEntity)
        );
    }

    @Test
    void testAddRoleToUserThatAlreadyHasThisRole() {
        UserEntity user = createTestUser();
        user.getRoleEntities().add(userRoleEntity);

        String result = roleManagementService.addRoleToUser(USER_USERNAME, ROLE_USER);

        assertThat(result).isEqualTo(SAME_ROLES_MESSAGE);
    }

    @Test
    void testCreateUserWithRoleAdmin() {
        AuthRequest authRequest = new AuthRequest(NEW_USER_USERNAME, NEW_USER_PASSWORD);

        String result = roleManagementService.createUserWithRole(authRequest, ROLE_ADMIN);
        List<RoleEntity> expectedRoles = List.of(userRoleEntity, adminRoleEntity);

        UserEntity createdUser = userEntityRepository.findByUsername(NEW_USER_USERNAME).get();

        assertAll(
                () -> assertThat(result).isEqualTo(String.format(CREATE_USER_RESPONSE, NEW_USER_USERNAME, ROLE_ADMIN)),
                () -> assertThat(createdUser.getRoleEntities())
                        .usingRecursiveComparison()
                        .ignoringCollectionOrder()
                        .isEqualTo(expectedRoles)
        );
    }

    @Test
    void testCreateUserWithRoleUser() {
        AuthRequest authRequest = new AuthRequest(NEW_USER_USERNAME, NEW_USER_PASSWORD);

        String result = roleManagementService.createUserWithRole(authRequest, ROLE_USER);
        List<RoleEntity> expectedRoles = List.of(userRoleEntity);

        UserEntity createdUser = userEntityRepository.findByUsername(NEW_USER_USERNAME).get();

        assertAll(
                () -> assertThat(result).isEqualTo(String.format(CREATE_USER_RESPONSE, NEW_USER_USERNAME, ROLE_USER)),
                () -> assertThat(createdUser.getRoleEntities())
                        .usingRecursiveComparison()
                        .isEqualTo(expectedRoles)
        );
    }

    @Test
    void testCreateUserWithRoleThrowsWasCreatedException() {
        AuthRequest authRequest = new AuthRequest(USER_USERNAME, USER_PASSWORD);

        createTestUser();

        assertThatThrownBy(() -> roleManagementService.createUserWithRole(authRequest, ROLE_ADMIN))
                .isInstanceOf(DuplicateDataException.class)
                .hasMessageContaining(USER_EXIST_MESSAGE);
    }

    private UserEntity createTestUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(USER_USERNAME);
        userEntity.setPassword(USER_PASSWORD);
        userEntityRepository.save(userEntity);
        return userEntity;
    }
}
